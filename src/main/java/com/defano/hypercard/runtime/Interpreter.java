/*
 * Interpreter
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.runtime;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypertalk.ast.common.*;
import com.defano.hypertalk.ast.statements.ExpressionStatement;
import com.defano.hypertalk.ast.statements.StatementList;
import com.google.common.base.Function;
import com.google.common.util.concurrent.*;
import com.defano.hypertalk.HyperTalkTreeVisitor;
import com.defano.hypertalk.HypertalkErrorListener;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.ast.functions.UserFunction;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtParseError;
import com.defano.hypertalk.exception.HtSyntaxException;
import com.defano.hypertalk.parser.HyperTalkLexer;
import com.defano.hypertalk.parser.HyperTalkParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import javax.swing.*;
import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;

/**
 * Interpreter class provides static methods for compiling a script (translate
 * into an AST) as well as executing a string as a script.
 */
public class Interpreter {

    private static final ThreadPoolExecutor scriptExecutor;
    private static final ListeningExecutorService listeningScriptExecutor;
    private static final ScheduledExecutorService idleTimeExecutor;

    static {
        scriptExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("script-executor-%d").build());
        listeningScriptExecutor = MoreExecutors.listeningDecorator(scriptExecutor);
        idleTimeExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("idle-executor-%d").build());

        idleTimeExecutor.scheduleAtFixedRate(() -> {
            int pendingHandlers = scriptExecutor.getActiveCount() + scriptExecutor.getQueue().size();
            if (pendingHandlers == 0) {
                GlobalContext.getContext().getGlobalProperties().resetProperties();
            }

        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    public static Script compile(String scriptText) throws HtException {
        HypertalkErrorListener errors = new HypertalkErrorListener();

        HyperTalkLexer lexer = new HyperTalkLexer(new ANTLRInputStream(scriptText));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        HyperTalkParser parser = new HyperTalkParser(tokens);
        parser.removeErrorListeners();        // don't log to console
        parser.addErrorListener(errors);

        try {
            ParseTree tree = parser.script();

            if (!errors.errors.isEmpty()) {
                throw errors.errors.get(0);
            }

            return (Script) new HyperTalkTreeVisitor().visit(tree);

        } catch (HtParseError e) {
            throw new HtSyntaxException("Didn't understand that.", e.lineNumber, e.columnNumber);
        } catch (Throwable e) {
            throw new HtException("Didn't understand that.");
        }
    }

    public static Value evaluate(String expression) {
        try {
            Statement statement = compile(expression).getStatements().list.get(0);
            if (statement instanceof ExpressionStatement) {
                return ((ExpressionStatement) statement).expression.evaluate();
            }
        } catch (Exception e) {}

        // Value of a non-expression is itself
        return new Value(expression);
    }

    public static boolean isExpressionStatement(String statement) throws HtException {
        return compile(statement).getStatements().list.get(0) instanceof ExpressionStatement;
    }

    /**
     * Executes a "command" handler in the given script. A command handler is a special handler whose
     * name is passed like a message from HyperCard, but represents a special event that can be trapped
     * and overridden in the script. (Command handlers include 'keyDown', 'returnInField', 'returnKey', etc.)
     *
     * Any part that implements a command handler must 'pass' the command handler back to HyperCard, otherwise
     * the default behavior of that command will not be executed. A script that does not override the command
     * is assumed to 'pass' it.
     *
     * @param me The part whose script is being executed.
     * @param script The script of the part
     * @param command The command handler name.
     * @return A future containing a boolean indicating if the handler has "trapped" the message.
     */
    public static ListenableFuture<Boolean> executeCommandHandler(PartSpecifier me, Script script, PassedCommand command, ExpressionList arguments) {
        NamedBlock handler = script.getHandler(command.messageName);

        if (handler == null) {
            return Futures.immediateFuture(false);
        } else {
            return Futures.transform(executeNamedBlock(me, handler, arguments), (Function<PassedCommand, Boolean>) input -> input != command);
        }
    }

    public static Future executeString(PartSpecifier me, String statementList) throws HtException  {
        return executeNamedBlock(me, getBlockForStatementList(compile(statementList).getStatements()), new ExpressionList());
    }

    public static void executeHandler(PartSpecifier me, Script script, String handler) {
        executeNamedBlock(me, script.getHandler(handler), new ExpressionList());
    }

    public static Value executeFunction(PartSpecifier me, UserFunction function, ExpressionList arguments) {
        FunctionExecutionTask functionTask = new FunctionExecutionTask(me, function, arguments);

        try {
            // Not normally possible user functions are always executed in the context of a handler
            if (SwingUtilities.isEventDispatchThread())
                return scriptExecutor.submit(functionTask).get();
            else
                return functionTask.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ListenableFuture<PassedCommand> executeNamedBlock(PartSpecifier me, NamedBlock handler, ExpressionList arguments) {
        HandlerExecutionTask handlerTask = new HandlerExecutionTask(me, handler, arguments);

        try {
            if (SwingUtilities.isEventDispatchThread())
                return listeningScriptExecutor.submit(handlerTask);
            else {
                return Futures.immediateFuture(handlerTask.call());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static NamedBlock getBlockForStatementList(StatementList statementList) {
        return new NamedBlock("", "", new ParameterList(), statementList);
    }

}
