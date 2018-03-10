package com.defano.wyldcard.runtime.interpreter;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.model.NamedBlock;
import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.ast.model.UserFunction;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.ExpressionStatement;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.ExitToHyperCardException;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.common.util.concurrent.*;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A facade and thread model for executing HyperTalk scripts. All script compilation and execution should flow through
 * this class to assure proper threading.
 */
public class Interpreter {

    private final static int MAX_COMPILE_THREADS = 6;          // Simultaneous background parse tasks
    private final static int MAX_EXECUTOR_THREADS = 8;         // Simultaneous scripts executing

    private static final ExecutorService messageExecutor = Executors.newSingleThreadExecutor();
    private static final ThreadPoolExecutor backgroundCompileExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_COMPILE_THREADS, new ThreadFactoryBuilder().setNameFormat("compiler-%d").build());
    private static final ThreadPoolExecutor scriptExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS, new ThreadFactoryBuilder().setNameFormat("script-executor-%d").build());
    private static final ListeningExecutorService listeningScriptExecutor = MoreExecutors.listeningDecorator(scriptExecutor);
    private static final ListeningExecutorService listeningMessageExecutor = MoreExecutors.listeningDecorator(messageExecutor);

    /**
     * Preemptively compiles the given script text on a background thread and invokes the CompileCompletionObserver
     * (on the background thread) when complete.
     * <p>
     * Note that this method cancels any previously requested compilation except the currently executing one (if one is
     * executing). Thus, invocation of the completion observer is not guaranteed; some jobs will be canceled
     * before they run and thus never complete.
     * <p>
     * This method is primarily useful for parse-as-you-type syntax checking.
     *
     * @param scriptText The script to parse.
     * @param observer   A non-null callback to fire when compilation is complete.
     */
    public static void asyncCompile(CompilationUnit compilationUnit, String scriptText, CompileCompletionObserver observer) {

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
    public static Script blockingCompileScriptlet(String scriptText) throws HtException {
        return (Script) TwoPhaseParser.parseScript(CompilationUnit.SCRIPTLET, scriptText);
    }

    /**
     * Compiles the given script on the current thread.
     *
     * @param scriptText The script text to parse.
     * @return The compiled Script object (the root of the abstract syntax tree)
     * @throws HtException Thrown if an error (i.e., syntax error) occurs when compiling.
     */
    public static Script blockingCompileScript(String scriptText) throws HtException {
        return (Script) TwoPhaseParser.parseScript(CompilationUnit.SCRIPT, scriptText);
    }

    /**
     * Evaluates a string as a HyperTalk expression on the current thread.
     *
     * @param expression The value of the evaluated text; based on HyperTalk language semantics, text that cannot be
     *                   evaluated or is not a legal expression evaluates to itself.
     * @return The Value of the evaluated expression.
     */
    public static Value blockingEvaluate(String expression) {
        try {
            Statement statement = blockingCompileScriptlet(expression).getStatements().list.get(0);
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
     * Attempts to evaluate the given value as an AST node identified by klass on the current thread.
     * <p>
     * The given value is compiled as a HyperTalk scriptlet and the first statement in the script is coerced to the
     * requested type. Returns null if the value is not a valid HyperTalk script or contains a script fragment that
     * cannot be coerced to the requested type.
     * <p>
     * For example, if value contains the text 'card field id 1', and klass is PartExp.class then an instance of
     * PartIdExp will be returned referring to the requested card field part.
     *
     * @param value The value to dereference; may be any non-null value, but only Values containing valid HyperTalk
     *              can successfully be de-referenced.
     * @param klass The Class to coerce/dereference the value into (may return a subtype of this class).
     * @param <T>   The type of the requested class.
     * @return Null if dereference fails for any reason, otherwise an instance of the requested class representing
     * the dereferenced value.
     */
    @SuppressWarnings("unchecked")
    public static <T> T blockingEvaluate(Value value, Class<T> klass) {
        try {
            Statement statement = Interpreter.blockingCompileScriptlet(value.stringValue()).getStatements().list.get(0);

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
     * Executes a user-defined function on the current thread and returns the result; may not be invoked on the Swing
     * dispatch thread.
     *
     * @param me        The part that the 'me' keyword refers to.
     * @param function  The compiled UserFunction
     * @param arguments The arguments to be passed to the function
     * @return The value returned by the function (an empty string if the function does not invoke 'return')
     * @throws HtSemanticException Thrown if an error occurs executing the function.
     */
    public static Value blockingExecuteFunction(PartSpecifier me, UserFunction function, Expression arguments) throws HtException {
        ThreadUtils.assertWorkerThread();
        return new FunctionExecutionTask(me, function, arguments).call();
    }

    /**
     * Executes a script handler on a background thread and notifies an observer when complete.
     * <p>
     * If the current thread is already a background thread (not the dispatch thread), the handler executes
     * synchronously on the current thread.
     * <p>
     * Any handler that does not 'pass' the command traps its behavior and prevents other scripts (or HyperCard) from
     * acting upon it. A script that does not implement the handler is assumed to 'pass' it.
     *
     * @param me                 The part whose script is being executed (for the purposes of the 'me' keyword).
     * @param script             The script of the part
     * @param command            The command handler name.
     * @param arguments          A list of expressions representing arguments passed with the message
     * @param completionObserver Invoked after the handler has executed on the same thread on which the handler ran.
     *                           Note that this observer will not fire if the script terminates as a result of an
     *                           exception or breakpoint.
     */
    public static void asyncExecuteHandler(PartSpecifier me, Script script, String command, ListExp arguments, HandlerCompletionObserver completionObserver) {
        NamedBlock handler = script == null ? null : script.getHandler(command);

        // Script does not have a handler for this message; create a "default" handler to pass it
        if (handler == null) {
            handler = NamedBlock.emptyPassBlock(command);
        }

        Futures.addCallback(asyncExecuteBlock(me, handler, arguments), new FutureCallback<String>() {
            @Override
            public void onSuccess(String passedMessage) {

                // Handler did not invoke pass: message was trapped
                if (completionObserver != null && passedMessage == null || passedMessage.isEmpty()) {
                    completionObserver.onHandlerRan(me, script, command, true);
                }

                // Handler invoked pass; message not trapped
                else if (completionObserver != null && passedMessage.equalsIgnoreCase(command)) {
                    completionObserver.onHandlerRan(me, script, command, false);
                }

                // Semantic error: Handler passed a message other than the one being handled.
                else {
                    WyldCard.getInstance().showErrorDialog(new HtSemanticException("Cannot pass a message other than the one being handled."));
                }

            }

            @Override
            public void onFailure(Throwable t) {

                if (t instanceof ExitToHyperCardException) {
                    // Nothing to do
                }

                // HyperTalk error occurred during execution
                else if (t instanceof HtException) {
                    WyldCard.getInstance().showErrorDialog((HtException) t);
                }

                // So other error occurred that we're ill-equipped to deal with
                else {
                    t.printStackTrace();
                    WyldCard.getInstance().showErrorDialog(new HtSemanticException("An unexpected error occurred."));
                }
            }
        });
    }

    /**
     * Evaluates text entered into the message box on a background thread and notifies an observer of the result when
     * complete.
     * <p>
     * Message evaluation is a special case of script execution:
     * <p>
     * 1. All messages entered into the message window share a single stack frame so that symbols created in one message
     * are available to the next. For example, 'put 10 into x' followed by 'put x' should result in 10. To achieve this,
     * all message evaluations must occur on the same thread, and a special execution task is required to prevent
     * producing a new stack frame during each evaluation.
     * <p>
     * 2. When evaluating from the message window, 'the target' returns the current card, not the message box (for
     * whatever reason). This results in a special case where the target is not the base of the 'me' stack.
     *
     * @param message            The message text to evaluate.
     * @param evaluationObserver A set of observer callbacks that fire (on the Swing dispatch thread) when evaluation is
     *                           complete.
     */
    public static void asyncEvaluateMessage(String message, MessageEvaluationObserver evaluationObserver) {

        Futures.addCallback(Futures.makeChecked(listeningMessageExecutor.submit(new MessageEvaluationTask(message)), new CheckedFutureExceptionMapper()), new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                SwingUtilities.invokeLater(() -> evaluationObserver.onMessageEvaluated(result));
            }

            @Override
            public void onFailure(Throwable t) {
                SwingUtilities.invokeLater(() -> evaluationObserver.onEvaluationError(new CheckedFutureExceptionMapper().apply((Exception) t)));
            }
        });
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
    public static CheckedFuture<String, HtException> asyncExecuteString(PartSpecifier me, String statementList) throws HtException {
        return asyncExecuteBlock(me, NamedBlock.anonymousBlock(blockingCompileScriptlet(statementList).getStatements()), new ListExp(null));
    }

    /**
     * Gets the number of scripts that are either actively executing or waiting to be executed. Returns 0 when HyperCard
     * is "idle". Does not include any script evaluation done via the message box.
     *
     * @return The number of active or pending scripts
     */
    public static int getPendingScriptCount() {
        return scriptExecutor.getActiveCount() + scriptExecutor.getQueue().size();
    }

    /**
     * Executes a named block on a background thread and returns a future to the name of the message passed from the
     * block (if any).
     * <p>
     * Executes on the current thread if the current thread is not the dispatch thread. If the current thread is the
     * dispatch thread, then submits execution to the scriptExecutor.
     *
     * @param me        The part that the 'me' keyword refers to.
     * @param handler   The block to execute
     * @param arguments The arguments to be passed to the block
     * @return A future to the name of the message passed from the block or null if no message was passed.
     */
    private static CheckedFuture<String, HtException> asyncExecuteBlock(PartSpecifier me, NamedBlock handler, ListExp arguments) {
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