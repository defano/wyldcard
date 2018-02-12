package com.defano.hypercard.runtime.interpreter;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.runtime.HandlerCompletionObserver;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.ExpressionStatement;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.common.base.Function;
import com.google.common.util.concurrent.*;

import javax.swing.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A facade and thread model for executing HyperTalk scripts. All script compilation and execution should flow through
 * this class to assure proper threading.
 */
public class Interpreter implements TwoPhaseParser {

    private final static int MAX_COMPILE_THREADS = 6;          // Simultaneous background parse tasks
    private final static int MAX_EXECUTOR_THREADS = 8;         // Simultaneous scripts executing

    private static final Executor messageExecutor;
    private static final ThreadPoolExecutor backgroundCompileExecutor;
    private static final ThreadPoolExecutor scriptExecutor;
    private static final ListeningExecutorService listeningScriptExecutor;

    static {
        messageExecutor = Executors.newSingleThreadExecutor();
        backgroundCompileExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_COMPILE_THREADS, new ThreadFactoryBuilder().setNameFormat("compiler-%d").build());
        scriptExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS, new ThreadFactoryBuilder().setNameFormat("script-executor-%d").build());
        listeningScriptExecutor = MoreExecutors.listeningDecorator(scriptExecutor);
    }

    /**
     * Preemptively compiles the given script on a background thread and invokes the CompileCompletionObserver
     * when complete.
     * <p>
     * Note that this method cancels any previously requested compilation except the currently executing compilation,
     * if one is executing. Thus, invocation of the completion observer is not guaranteed. Some jobs will be canceled
     * before they run and thus never complete.
     * <p>
     * This method is primarily useful for parse-as-you-type syntax checking.
     *
     * @param scriptText The script to parse.
     * @param observer   A non-null callback to fire when compilation is complete.
     */
    public static void compileInBackground(CompilationUnit compilationUnit, String scriptText, CompileCompletionObserver observer) {

        // Preempt any previously enqueued parse jobs
        backgroundCompileExecutor.getQueue().clear();

        backgroundCompileExecutor.submit(() -> {
            HtException generatedError = null;
            Object compiledScript = null;

            try {
                compiledScript = TwoPhaseParser.parseScript(compilationUnit, scriptText);
            } catch (HtException e) {
                generatedError = e;
            }

            observer.onCompileCompleted(scriptText, compiledScript, generatedError);
        });
    }

    /**
     * Compiles the given "scriptlet" on the current thread.
     *
     * @param scriptText The script text to parse.
     * @return The compiled Script object (the root of the abstract syntax tree)
     * @throws HtException Thrown if an error (i.e., syntax error) occurs when compiling.
     */
    public static Script compileScriptlet(String scriptText) throws HtException {
        return (Script) TwoPhaseParser.parseScript(CompilationUnit.SCRIPTLET, scriptText);
    }

    /**
     * Compiles the given script on the current thread.
     *
     * @param scriptText The script text to parse.
     * @return The compiled Script object (the root of the abstract syntax tree)
     * @throws HtException Thrown if an error (i.e., syntax error) occurs when compiling.
     */
    public static Script compileScript(String scriptText) throws HtException {
        return (Script) TwoPhaseParser.parseScript(CompilationUnit.SCRIPT, scriptText);
    }

    /**
     * Evaluates a string as a HyperTalk expression on the current thread.
     *
     * @param expression The value of the evaluated text; based on HyperTalk language semantics, text that cannot be
     *                   evaluated or is not a legal expression evaluates to itself.
     * @return The Value of the evaluated expression.
     */
    public static Value evaluate(String expression) {
        try {
            Statement statement = compileScriptlet(expression).getStatements().list.get(0);
            if (statement instanceof ExpressionStatement) {
                return ((ExpressionStatement) statement).expression.evaluate();
            }
        } catch (Exception e) {
            // Nothing to do; okay to evaluate bogus text
        }

        // Value of a non-expression is itself
        return new Value(expression);
    }

    /**
     * Attempts to evaluate the given value as an AST node identified by klass on the current thread. The given value is
     * compiled as a HyperTalk scriptlet and the first statement in the script is coerced to the requested type. Returns
     * null if the value is not a valid HyperTalk script or contains a script fragment that cannot be coerced to
     * the requested type.
     * <p>
     * For example, if value contains the text 'card field id 1', and klass is PartExp.class then an instance of
     * PartIdExp will be returned referring to the requested part.
     *
     * @param value The value to dereference; may be any non-null value, but only Values containing valid HyperTalk
     *              can be de-referenced.
     * @param klass The Class to coerce/dereference the value into (may return a subtype of this class).
     * @param <T>   The type of the requested class.
     * @return Null if dereference fails for any reason, otherwise an instance of the requested class representing
     * the dereferenced value.
     */
    @SuppressWarnings("unchecked")
    public static <T> T evaluate(Value value, Class<T> klass) {
        try {
            Statement statement = Interpreter.compileScriptlet(value.stringValue()).getStatements().list.get(0);

            // Simple case; statement matches requested type
            if (statement.getClass().isAssignableFrom(klass)) {
                return (T) statement;
            } else if (Expression.class.isAssignableFrom(klass)) {
                Expression expression = ((ExpressionStatement) statement).expression;
                if (klass.isAssignableFrom(expression.getClass())) {
                    return (T) expression;
                }
            }

        } catch (Exception e) {
            // Nothing to do
        }

        return null;
    }

    /**
     * Determines if a given scriptlet represents a valid HyperTalk expression; evaluation executes on the current
     * thread.
     *
     * @param statement Text to onEvaluate
     * @return True if the statement is a valid expression; false if it is not.
     * @throws HtException Thrown if the statement cannot be compiled (due to a syntax/semantic error).
     */
    public static boolean isExpressionStatement(String statement) throws HtException {
        return compileScriptlet(statement).getStatements().list.get(0) instanceof ExpressionStatement;
    }

    /**
     * Executes a handler in a given script on a background thread.
     *
     * If the current thread is already a background thread (not the dispatch thread), the handler executes
     * synchronously on the current thread.
     * <p>
     * Any handler that does not 'pass' the command traps its behavior and prevents other scripts (or HyperCard) from
     * acting upon it. A script that does not implement the handler is assumed to 'pass' it.
     *
     * @param me      The part whose script is being executed (for the purposes of the 'me' keyword).
     * @param script  The script of the part
     * @param command The command handler name.
     * @param arguments A list of expressions representing arguments passed with the message
     * @param completionObserver Invoked after the handler has executed on the same thread on which the handler ran
     */
    public static void executeHandler(PartSpecifier me, Script script, String command, ExpressionList arguments, HandlerCompletionObserver completionObserver) {
        NamedBlock handler = script.getHandler(command);

        // Script does not have a handler for this message; create a "default" handler to pass it
        if (handler == null) {
            handler = NamedBlock.emptyPassBlock(command);
        }

        Futures.transform(executeNamedBlock(me, handler, arguments), (Function<String, Void>) passedMessage -> {

            // Handler did not invoke pass: message was trapped
            if (completionObserver != null && passedMessage == null || passedMessage.isEmpty()) {
                completionObserver.onHandlerRan(true);
            }

            // Handler invoked pass; message not trapped
            else if (completionObserver != null && passedMessage.equalsIgnoreCase(command)) {
                completionObserver.onHandlerRan(false);
            }

            // Semantic error: Handler passed a message other than the one being handled.
            else {
                HyperCard.getInstance().showErrorDialog(new HtSemanticException("Cannot pass a message other than the one being handled."));
            }

            return null;
        });
    }

    /**
     * Synchronously executes a compiled user function (blocks the current thread until execution is complete). May
     * not be invoked on the Swing dispatch thread.
     *
     * @param me        The part that the 'me' keyword refers to.
     * @param function  The compiled UserFunction
     * @param arguments The arguments to be passed to the function
     * @return The value returned by the function (an empty string if the function does not invoke 'return')
     * @throws HtSemanticException Thrown if an error occurs executing the function.
     */
    public static Value executeFunction(PartSpecifier me, UserFunction function, ExpressionList arguments) throws HtException {
        ThreadUtils.assertWorkerThread();

        try {
            return new FunctionExecutionTask(me, function, arguments).call();
        } catch (HtException e) {
            throw new HtSemanticException(e.getMessage(), e);
        }
    }

    /**
     * Compiles a list of HyperTalk statements on the current thread, then executes the compiled script om a background
     * thread. Produces a CheckFuture providing the name of the message passed by the script, or null, if no message
     * is passed.
     *
     * @param me            The part that the 'me' keyword refers to.
     * @param statementList The list of statements.
     * @return A CheckedFuture to the name passed from the script or null if no name was passed, throwing an
     * HtException if an error occurs while executing the script.
     * @throws HtException Thrown if an error occurs compiling the statements.
     */
    public static CheckedFuture<String, HtException> executeString(PartSpecifier me, String statementList) throws HtException {
        return executeNamedBlock(me, NamedBlock.anonymousBlock(compileScriptlet(statementList).getStatements()), new ExpressionList());
    }

    /**
     * Gets the executor used to evaluate the contents of the message box.
     *
     * @return The message box executor.
     */
    public static Executor getMessageExecutor() {
        return messageExecutor;
    }

    /**
     * Gets the number of scripts that are either actively executing or waiting to be executed. Returns 0 when HyperCard
     * is "idle".
     *
     * @return The number of active or pending scripts
     */
    public static int getPendingScriptCount() {
        return scriptExecutor.getActiveCount() + scriptExecutor.getQueue().size();
    }

    /**
     * Executes a named block and returns a future to the name of the message passed from the block (if any).
     * <p>
     * Executes on the current thread if the current thread is not the dispatch thread. If the current thread is the
     * dispatch thread, then submits execution to the scriptExecutor.
     *
     * @param me        The part that the 'me' keyword refers to.
     * @param handler   The block to execute
     * @param arguments The arguments to be passed to the block
     * @return A future to the name of the message passed from the block or null if no message was passed.
     * @throws HtSemanticException Thrown if an error occurs executing the block
     */
    private static CheckedFuture<String, HtException> executeNamedBlock(PartSpecifier me, NamedBlock handler, ExpressionList arguments) {
        HandlerExecutionTask handlerTask = new HandlerExecutionTask(me, SwingUtilities.isEventDispatchThread(), handler, arguments);

        if (SwingUtilities.isEventDispatchThread()) {
            return Futures.makeChecked(listeningScriptExecutor.submit(handlerTask), new CheckedFutureExceptionMapper());
        } else {
            try {
                return Futures.makeChecked(Futures.immediateFuture(handlerTask.call()), new CheckedFutureExceptionMapper());
            } catch (HtException e) {
                return Futures.makeChecked(Futures.immediateFailedCheckedFuture(e), new CheckedFutureExceptionMapper());
            }
        }
    }
}
