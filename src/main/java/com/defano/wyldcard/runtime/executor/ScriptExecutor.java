package com.defano.wyldcard.runtime.executor;

import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.NamedBlock;
import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.ast.statement.ExpressionStatement;
import com.defano.hypertalk.ast.statement.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.debug.message.HandlerInvocation;
import com.defano.wyldcard.debug.message.HandlerInvocationCache;
import com.defano.wyldcard.message.Message;
import com.defano.wyldcard.message.MessageBuilder;
import com.defano.wyldcard.message.SystemMessage;
import com.defano.wyldcard.runtime.compiler.*;
import com.defano.wyldcard.runtime.executor.observer.HandlerCompletionObserver;
import com.defano.wyldcard.runtime.executor.observer.HandlerExecutionFutureCallback;
import com.defano.wyldcard.runtime.executor.observer.MessageEvaluationObserver;
import com.defano.wyldcard.runtime.executor.task.FunctionHandlerExecutionTask;
import com.defano.wyldcard.runtime.executor.task.HandlerExecutionTask;
import com.defano.wyldcard.runtime.executor.task.MessageHandlerExecutionTask;
import com.defano.wyldcard.runtime.executor.task.StaticContextEvaluationTask;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.ThreadChecker;
import com.google.common.util.concurrent.*;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A facade and thread model for executing HyperTalk scripts. All script compilation and execution should flow through
 * this class to assure proper threading.
 */
@SuppressWarnings("UnstableApiUsage")
public class ScriptExecutor {

    // Executor for the message box and "evaluate expression" window
    private static final ExecutorService staticExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("script-exe-msg").build());
    private static final ListeningExecutorService listeningStaticExecutor = MoreExecutors.listeningDecorator(staticExecutor);

    // Executor for special messages that require guaranteed ordering
    private static final ExecutorService orderedExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("script-exe-ordered").build());
    private static final ListeningExecutorService listeningOrderedExecutor = MoreExecutors.listeningDecorator(orderedExecutor);

    // Executor for all other HyperTalk scripts and handlers
    private final static int MAX_EXECUTOR_THREADS = 8;
    private static final ThreadPoolExecutor scriptExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS, new ThreadFactoryBuilder().setNameFormat("script-exe-%d").build());
    private static final ListeningExecutorService listeningScriptExecutor = MoreExecutors.listeningDecorator(scriptExecutor);

    /**
     * Returns the result of evaluating a string as a HyperTalk expression on the current thread. An expression that
     * cannot be evaluated (i.e., invalid syntax or an execution error occurs) results in the script text itself.
     * <p>
     * This method may not be executed on the Swing dispatch thread.
     *
     * @param expression The value of the evaluated text; based on HyperTalk language semantics, text that cannot be
     *                   evaluated or is not a legal expression evaluates to itself.
     * @param context    The execution context
     * @return The Value of the evaluated expression.
     */
    public static Value blockingEvaluate(String expression, ExecutionContext context) {
        ThreadChecker.assertWorkerThread();

        try {
            Statement statement = ((Script) ScriptCompiler.blockingCompile(CompilationUnit.SCRIPTLET, expression)).getStatements().list.get(0);
            if (statement instanceof ExpressionStatement) {
                return ((ExpressionStatement) statement).expression.evaluate(context);
            }
        } catch (Exception e) {
            // Nothing to do; okay to evaluate bogus text
        }

        // Value of a non-expression is itself
        return new Value(expression);
    }

    /**
     * Attempts to evaluate the given value as an AST node identified by klass on the current thread. May not be
     * executed on the Swing dispatch thread.
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
    public static <T> T blockingDereference(Value value, Class<T> klass) {
        ThreadChecker.assertWorkerThread();

        try {
            Statement statement = ((Script) ScriptCompiler.blockingCompile(CompilationUnit.SCRIPTLET, value.toString())).getStatements().list.get(0);

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
            // Nothing to do; value was either not valid HyperTalk or not coercible to requested type
        }

        return null;
    }

    /**
     * Executes a user-defined function on the current thread and returns the result; may not be invoked from the Swing
     * dispatch thread.
     *
     * @param context   The execution context
     * @param me        The part that the 'me' keyword refers to.
     * @param function  The compiled UserFunction
     * @param arguments The arguments to be passed to the function
     * @return The value returned by the function (an empty string if the function does not invoke 'return')
     * @throws HtSemanticException Thrown if an error occurs executing the function.
     */
    public static Value blockingExecuteFunction(ExecutionContext context, ASTNode callingNode, PartSpecifier me, NamedBlock function, List<Value> arguments) throws HtException {
        ThreadChecker.assertWorkerThread();
        return new FunctionHandlerExecutionTask(context, callingNode, me, function, arguments).call();
    }

    /**
     * Executes a script handler on a background thread and notifies an observer when complete.
     * <p>
     * Note that this method is asynchronous only when invoked from the Swing dispatch thread (or any thread not
     * associated with script execution). Script executors that invoke this method will block pending completion of the
     * executed handler.
     * <p>
     * Any handler that does not 'pass' the handler name traps its behavior and prevents other scripts (or WyldCard)
     * from acting upon it. A script that does not implement a handler for a given message is assumed to 'pass' it.
     *
     * @param context            The execution context
     * @param me                 The part whose script is being executed (for the purposes of the 'me' keyword).
     * @param script             The script of the part
     * @param message            The message whose handler should be executed.
     * @param completionObserver Invoked after the handler has executed on the same thread on which the handler ran.
     */
    public static void asyncExecuteHandler(ExecutionContext context, ASTNode callingNode, PartSpecifier me, Script script, Message message, HandlerCompletionObserver completionObserver) {

        // Find handler for message in the script
        NamedBlock handler = script == null ? null : script.getHandler(message.getMessageName());
        CheckedFuture<Boolean, HtException> future;

        // Script implements handler for message; execute it
        if (handler != null) {
            future = submit(getExecutorForMessage(message), new MessageHandlerExecutionTask(context, callingNode, me, handler, message));
        }

        // Special case: No handler in the script for this message; produce a "no-op" execution
        else {
            future = submit(getExecutorForMessage(message), () -> {

                // Synthesize handler invocation for message watcher
                HandlerInvocationCache.getInstance().notifyMessageHandled(new HandlerInvocation(
                        Thread.currentThread().getName(),
                        message.getMessageName(),
                        message.evaluateArguments(context),
                        me,
                        context.getTarget() == null,
                        context.getStackDepth(),
                        false)
                );

                // No handler for script, but we still need to capture that this part was the target
                if (context.getTarget() == null) {
                    context.setTarget(me);
                }

                // Unimplemented handlers don't trap message
                return false;
            });
        }

        Futures.addCallback(future, new HandlerExecutionFutureCallback(me, script, message.getMessageName(), completionObserver));
    }

    /**
     * Evaluates text using a specified context on a background thread and notifies an observer of the result when
     * complete. This is primarily useful for message window text evaluation (also used in the "Evaluate Expression"
     * feature of the debugger).
     * <p>
     * In-context evaluation is a special case of script execution:
     * <p>
     * 1. All script text evaluated with this method share a single stack frame so that symbols created in one call
     * are available to the next. For example, 'put 10 into x' followed by 'put x' should result in 10. To achieve this,
     * all such evaluations must occur in the same execution context and a special execution task is utilized to
     * prevent pushing a new stack frame during each evaluation.
     * <p>
     * 2. When evaluating from this method, 'the target' returns the current card, not the message box (for
     * whatever reason). This results in a special case where the target is not the base of the 'me' stack.
     * <p>
     * 3. When multiple statements are evaluated using this method, the last statement is interpreted as an expression
     * and the evaluation of that expression is returned as the result. This has the otherwise unusual behavior of
     * treating a literal symbol value as the last statement as a request to evaluate the symbol as a variable. For
     * example, if the last statement evaluated is "doSomething", HyperTalk will typically interpret that as a message
     * to be sent; but in this case, it will be sent as a message, and then evaluated as a variable returning to the
     * observer whatever symbolic value "doSomething" has been assigned, or the literal "doSomething" if no value is
     * bound to it.
     *
     * @param staticContext      The execution context under which the text should be evaluated
     * @param message            The message text to evaluate.
     * @param evaluationObserver A set of observer callbacks that fire (on the Swing dispatch thread) when evaluation is
     *                           complete.
     */
    public static void asyncStaticContextEvaluate(ExecutionContext staticContext, String message, MessageEvaluationObserver evaluationObserver) {

        Futures.addCallback(Futures.makeChecked(listeningStaticExecutor.submit(new StaticContextEvaluationTask(staticContext, message)), new CheckedFutureExceptionMapper()), new FutureCallback<String>() {
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
     * @param context       The execution context
     * @param me            The part that the 'me' keyword refers to.
     * @param statementList The list of statements.
     * @return A CheckedFuture to the name passed from the script or null if no name was passed, throwing an
     * HtException if an error occurs while executing the script.
     * @throws HtException Thrown if an error occurs compiling the statements.
     */
    public static CheckedFuture<Boolean, HtException> asyncExecuteString(ExecutionContext context, PartSpecifier me, String statementList) throws HtException {
        return submit(listeningScriptExecutor, new MessageHandlerExecutionTask(context, null, me, NamedBlock.anonymousBlock(((Script) ScriptCompiler.blockingCompile(CompilationUnit.SCRIPTLET, statementList)).getStatements()), MessageBuilder.emptyMessage()));
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
     * Determines if the current thread is a known script execution thread.
     *
     * @return True if the current thread is a script-executor thread.
     */
    private static boolean isScriptExecutorThread() {
        return Thread.currentThread().getName().startsWith("script-exe-");
    }

    /**
     * Gets a CheckedFuture representing the future result of executing a script handler (a boolean value indicating
     * whether the handler trapped the message, or, an {@link HtException} if the script failed to complete executing).
     * <p>
     * If this thread is a script execution thread, the handler is executed synchronously on this thread and the result
     * is returned as an immediate future.
     * <p>
     * If this thread is not a script execution thread (e.g., the Swing dispatch thread), then the handler task is
     * submitted for execution by one of the available script executor threads.
     *
     * @param executor    The executor service that should be used to execute this handler.
     * @param handlerTask The handler task to execute
     * @return A future (placeholder) for the result of executing the handler (which may occur after some delay). The
     * success disposition provides a boolean indicating whether the handler "trapped" the message; the failure
     * disposition provides a {@link HtException} describing why the handler failed to execute.
     */
    private static CheckedFuture<Boolean, HtException> submit(ListeningExecutorService executor, HandlerExecutionTask handlerTask) {
        if (isScriptExecutorThread()) {
            try {
                return Futures.makeChecked(Futures.immediateFuture(handlerTask.call()), new CheckedFutureExceptionMapper());
            } catch (HtException e) {
                return Futures.makeChecked(Futures.immediateFailedCheckedFuture(e), new CheckedFutureExceptionMapper());
            }
        } else {
            return Futures.makeChecked(executor.submit(handlerTask), new CheckedFutureExceptionMapper());
        }
    }

    /**
     * Gets the preferred executor service to use when executing the handler for the given message. Provides a shared,
     * single-threaded executor for messages that have a guaranteed ordering (like open, close, suspend and resume),
     * returns the shared (pooled) executor for other messages.
     *
     * @param m The message whose executor should be retrieved.
     * @return The executor on which to submit the handler task associated with this message.
     */
    private static ListeningExecutorService getExecutorForMessage(Message m) {
        if (m instanceof SystemMessage && ((SystemMessage) m).isOrderGuaranteed()) {
            return listeningOrderedExecutor;
        } else {
            return listeningScriptExecutor;
        }
    }

}