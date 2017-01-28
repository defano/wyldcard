/**
 * Interpreter.java
 * @author matt.defano@gmail.com
 * 
 * Interpreter class provides static methods for compiling a script (translate
 * into an AST) as well as executing a string as a script.
 */

package hypercard.runtime;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import hypertalk.HyperTalkTreeVisitor;
import hypertalk.HypertalkErrorListener;
import hypertalk.ast.common.ExpressionList;
import hypertalk.ast.common.Script;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.ast.functions.UserFunction;
import hypertalk.ast.statements.StatExp;
import hypertalk.ast.statements.StatementList;
import hypertalk.exception.HtException;
import hypertalk.exception.HtParseError;
import hypertalk.exception.HtSyntaxException;
import hypertalk.parser.HyperTalkLexer;
import hypertalk.parser.HyperTalkParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Interpreter {

    private static ExecutorService scriptExecutor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("script-executor-%d").build());

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
            throw new HtSyntaxException(e.getMessage(), e.lineNumber, e.columnNumber);
        }
    }

    public static boolean isExpressionStatement(String statement) throws HtException {
        return compile(statement).getStatements().list.get(0) instanceof StatExp;
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
