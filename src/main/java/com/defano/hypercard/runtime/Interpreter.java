package com.defano.hypercard.runtime;

import com.defano.hypercard.HyperCard;
import com.defano.hypertalk.ast.common.*;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.ExpressionStatement;
import com.defano.hypertalk.ast.statements.StatementList;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.HtSyntaxException;
import com.google.common.base.Function;
import com.google.common.util.concurrent.*;
import com.defano.hypertalk.HyperTalkTreeVisitor;
import com.defano.hypertalk.HyperTalkErrorListener;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.functions.UserFunction;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.parser.HyperTalkLexer;
import com.defano.hypertalk.parser.HyperTalkParser;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;

import javax.swing.*;
import java.util.concurrent.*;

/**
 * A facade and thread model for executing HyperTalk scripts. All script compilation and execution should flow through
 * this class to assure proper threading.
 */
public class Interpreter {

    private final static int MAX_COMPILE_THREADS  = 1;          // Simultaneous background compile tasks
    private final static int MAX_EXECUTOR_THREADS = 12;         // Simultaneous scripts executing
    private final static int MAX_LISTENER_THREADS = 12;         // Simultaneous listeners waiting for handler completion

    private static final Executor messageExecutor;
    private static final ThreadPoolExecutor backgroundCompileExecutor;
    private static final ThreadPoolExecutor scriptExecutor;
    private static final ThreadPoolExecutor completionListenerExecutor;

    private static final ListeningExecutorService listeningScriptExecutor;

    static {
        messageExecutor = Executors.newSingleThreadExecutor();
        completionListenerExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_LISTENER_THREADS, new ThreadFactoryBuilder().setNameFormat("completion-listener-%d").build());
        backgroundCompileExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_COMPILE_THREADS, new ThreadFactoryBuilder().setNameFormat("compiler-%d").build());
        scriptExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS, new ThreadFactoryBuilder().setNameFormat("script-executor-%d").build());
        listeningScriptExecutor = MoreExecutors.listeningDecorator(scriptExecutor);
    }

    /**
     * Preemptively compiles the given script on a background compile thread and invokes the CompileCompletionObserver
     * when complete.
     *
     * Note that this method cancels any previously requested compilation except the currently executing compilation,
     * if one is executing. Thus, invocation of the observer to indicate completion is not guaranteed. Some jobs will
     * be canceled before they run and thus never complete.
     *
     * This method is primarily useful for compile-as-you-type syntax checking.
     *
     * @param scriptText The script to compile.
     * @param observer A non-null callback to fire when compilation is complete.
     */
    public static void compileInBackground(CompilationUnit compilationUnit, String scriptText, CompileCompletionObserver observer) {

        // Preempt any previously enqueued compile jobs
        backgroundCompileExecutor.getQueue().clear();

        backgroundCompileExecutor.submit(() -> {
            HtException generatedError = null;
            Script compiledScript = null;

            try {
                compiledScript = compile(compilationUnit, scriptText);
            } catch (HtException e) {
                generatedError = e;
            }

            observer.onCompileCompleted(scriptText, compiledScript, generatedError);
        });
    }

    /**
     * Compiles the given script on the current thread.
     *
     * @param scriptText The script text to compile
     * @return The compiled Script object
     * @throws HtException Thrown if an error (i.e., syntax error) occurs when compiling.
     */
    public static Script compile(CompilationUnit compilationUnit, String scriptText) throws HtException {
        HyperTalkErrorListener errors = new HyperTalkErrorListener();

        HyperTalkLexer lexer = new HyperTalkLexer(new CaseInsensitiveInputStream(scriptText));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        HyperTalkParser parser = new HyperTalkParser(tokens);
        parser.removeErrorListeners();        // don't log to console
        parser.addErrorListener(errors);

        try {
            ParseTree tree = compilationUnit.getParseTree(parser);

            if (!errors.errors.isEmpty()) {
                throw errors.errors.get(0);
            }

            return (Script) new HyperTalkTreeVisitor().visit(tree);
        } catch (RecognitionException e) {
            throw new HtSyntaxException(e);
        }
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
            Statement statement = compile(CompilationUnit.SCRIPTLET, expression).getStatements().list.get(0);
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
     * Attempts to evaluate the given value as an AST node identified by klass. That is, the given value is compiled
     * as a HyperTalk script and the first and only statement in the script is coerced to the requested type. Returns
     * null if the value is not a valid HyperTalk script or contains a script fragment that cannot be coerced to
     * the requested type.
     *
     * For example, if value contains the text 'card field id 1', and klass is PartExp.class then an instance of
     * PartIdExp will be returned referring to the requested part.
     *
     * @param value The value to dereference; may be any non-null value, but only Values containing valid HyperTalk
     *              can be dereferenced.
     * @param klass The Class to coerce/dereference the value into (may return a subtype of this class).
     * @param <T> The type of the requested class.
     * @return Null if dereference fails for any reason, otherwise an instance of the requested class representing
     * the dereferenced value.
     */
    @SuppressWarnings("unchecked")
    public static <T> T dereference(Value value, Class<T> klass) {
        try {
            Statement statement = Interpreter.compile(CompilationUnit.SCRIPTLET, value.stringValue()).getStatements().list.get(0);

            // Simple case; statement matches requested type
            if (statement.getClass().isAssignableFrom(klass)) {
                return (T) statement;
            }

            else if (Expression.class.isAssignableFrom(klass)) {
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
     * Determines if the given Script text represents a valid HyperTalk expression on the current thread.
     *
     * @param statement Text to onEvaluate
     * @return True if the statement is a valid expression; false if it is not.
     * @throws HtException Thrown if the statement cannot be compiled (due to a syntax/semantic error).
     */
    public static boolean isExpressionStatement(String statement) throws HtException {
        return compile(CompilationUnit.SCRIPTLET, statement).getStatements().list.get(0) instanceof ExpressionStatement;
    }

    /**
     * Executes a handler in the given script on a background thread, returning a future indicating whether or not the
     * script trapped the message.
     *
     * Any handler that does not 'pass' the command traps its behavior and prevents other scripts (or HyperCard) from
     * acting upon it. A script that does not implement the handler is assumed to 'pass' it.
     *
     * @param me The part whose script is being executed (for the purposes of the 'me' keyword).
     * @param script The script of the part
     * @param command The command handler name.
     * @return A future containing a boolean indicating if the handler has "trapped" the message. Returns null if the
     * scripts attempts to pass a message other than the message being handled.
     */
    public static CheckedFuture<Boolean,HtException> executeHandler(PartSpecifier me, Script script, String command, ExpressionList arguments) {
        NamedBlock handler = script.getHandler(command);

        if (handler == null) {
            return Futures.makeChecked(Futures.immediateFuture(false), new CheckedFutureExceptionMapper());
        } else {
            try {
                return Futures.makeChecked(Futures.transform(executeNamedBlock(me, handler, arguments), (Function<String, Boolean>) passedMessage -> {

                    // Did not invoke pass: handler trapped message
                    if (passedMessage == null || passedMessage.isEmpty()) {
                        return true;
                    }

                    // Invoked pass; did not trap message
                    if (passedMessage.equalsIgnoreCase(command)) {
                        return false;
                    }

                    // Semantic error: Passing a message other than the handled message is disallowed.
                    HyperCard.getInstance().showErrorDialog(new HtSemanticException("Cannot pass a message other than the one being handled."));
                    return true;
                }), new CheckedFutureExceptionMapper());
            } catch (HtSemanticException e) {
                return Futures.immediateFailedCheckedFuture(e);
            }
        }
    }

    /**
     * Executes a list of HyperTalk statements on a background thread and returns the name of message passed (if any).
     *
     * @param me The part that the 'me' keyword refers to.
     * @param statementList The list of statements.
     * @return A future to the name passed from the script or null if no name was passed.
     * @throws HtException Thrown if an error occurs compiling the statements.
     */
    public static Future<String> executeString(PartSpecifier me, String statementList) throws HtException  {
        return executeNamedBlock(me, getAnonymousBlock(compile(CompilationUnit.SCRIPTLET, statementList).getStatements()), new ExpressionList());
    }

    /**
     * Synchronously executes a compiled user function (blocks the current thread until execution is complete).
     *
     * Executes the function on the current thread, unless the current thread is the dispatch thread (in which case the
     * function executes on a new thread, but the current thread is blocked pending its completion).
     *
     * @param me The part that the 'me' keyword refers to.
     * @param function The compiled UserFunction
     * @param arguments The arguments to be passed to the function
     * @return The value returned by the function (an empty string if the function does not invoke 'return')
     * @throws HtSemanticException Thrown if an error occurs executing the function.
     */
    public static Value executeFunction(PartSpecifier me, UserFunction function, ExpressionList arguments) throws HtException {
        FunctionExecutionTask functionTask = new FunctionExecutionTask(me, function, arguments);

        try {
            // Not normally possible, since user functions are always executed in the context of a handler
            if (SwingUtilities.isEventDispatchThread())
                return scriptExecutor.submit(functionTask).get();
            else
                return functionTask.call();
        } catch (HtException e) {
            throw new HtSemanticException(e.getMessage(), e);
        } catch (InterruptedException | ExecutionException e) {
            throw new HtException("An unexpected error occurred executing the function.");
        }
    }

    /**
     * Gets the executor used to execute scripts handling a HyperCard system message (i.e., when 'mouseUp' or 'idle'
     * is sent to a part).
     *
     * @return The message handler executor.
     */
    public static Executor getCompletionListenerExecutor() {
        return completionListenerExecutor;
    }

    /**
     * Gets the executor used to onEvaluate the contents of the message box.
     * @return The message box executor.
     */
    public static Executor getMessageExecutor() {
        return messageExecutor;
    }

    public static int getPendingScriptCount() {
        return scriptExecutor.getActiveCount() + scriptExecutor.getQueue().size();
    }

    /**
     * Executes a named block and returns a future to the name of the message passed from the block (if any).
     *
     * Executes on the current thread if the current thread is not the dispatch thread. If the current thread is the
     * dispatch thread, then submits execution to the scriptExecutor.
     *
     * @param me The part that the 'me' keyword refers to.
     * @param handler The block to execute
     * @param arguments The arguments to be passed to the block
     * @return A future to the name of the message passed from the block or null if no message was passed.
     * @throws HtSemanticException Thrown if an error occurs executing the block
     */
    private static CheckedFuture<String, HtException> executeNamedBlock(PartSpecifier me, NamedBlock handler, ExpressionList arguments) throws HtSemanticException {
        HandlerExecutionTask handlerTask = new HandlerExecutionTask(me, handler, arguments);

        try {
            if (SwingUtilities.isEventDispatchThread())
                return Futures.makeChecked(listeningScriptExecutor.submit(handlerTask), new CheckedFutureExceptionMapper());
            else {
                return Futures.makeChecked(Futures.immediateFuture(handlerTask.call()), new CheckedFutureExceptionMapper());
            }
        } catch (HtException e) {
            throw new HtSemanticException(e);
        }
    }

    /**
     * Wraps a list of statements in an anonymous NamedBlock object.
     *
     * @param statementList The list of statements
     * @return A NamedBlock representing the
     */
    private static NamedBlock getAnonymousBlock(StatementList statementList) {
        return new NamedBlock("", "", new ParameterList(), statementList);
    }

}
