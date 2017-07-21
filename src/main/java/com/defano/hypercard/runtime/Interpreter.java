/*
 * Interpreter
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.runtime;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypertalk.ast.statements.ExpressionStatement;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.defano.hypertalk.HyperTalkTreeVisitor;
import com.defano.hypertalk.HypertalkErrorListener;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Script;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.ast.functions.UserFunction;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.ast.statements.StatementList;
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

/**
 * Interpreter class provides static methods for compiling a script (translate
 * into an AST) as well as executing a string as a script.
 */
public class Interpreter {

    private static ThreadPoolExecutor scriptExecutor;
    private static ScheduledExecutorService idleTimeExecutor;

    static {
        scriptExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("script-executor-%d").build());
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

    public static Future executeString(PartSpecifier me, String statementList) throws HtException  {
        return executeStatementList(me, compile(statementList).getStatements(), true);
    }

    public static void executeHandler(PartSpecifier me, Script script, String handler) {
        executeStatementList(me, script.getHandler(handler), true);
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

    private static Future executeStatementList(PartSpecifier me, StatementList handler, boolean onNewThread) {
        HandlerExecutionTask handlerTask = new HandlerExecutionTask(me, handler);
        if (SwingUtilities.isEventDispatchThread() || onNewThread)
            return scriptExecutor.submit(handlerTask);
        else {
            handlerTask.run();
            return Futures.immediateFuture(null);
        }
    }

}
