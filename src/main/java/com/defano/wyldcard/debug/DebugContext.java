package com.defano.wyldcard.debug;

import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.runtime.StackFrame;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.layouts.ScriptEditor;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import javax.swing.*;

/**
 * Represents the state of the WyldCard debugger.
 * <p>
 * Note that WyldCard is multi-threaded, but this debugger only support debugging a single script execution thread at
 * any given time. If the debugger is active (i.e., {@link #isDebugging()}) and a breakpoint is reached on a thread
 * other than the one currently being debugged, that breakpoint is simply ignored.
 * <p>
 * Prior to executing any statement, the HyperTalk AST is responsible for checking if the statement represents a
 * breakpoint (via {@link #isBreakpoint(ExecutionContext, Statement)}. If this check returns true, the statement
 * should call {@link #debug(ExecutionContext, Statement)} to activate the debugger. A call to this method will pause
 * execution of the calling thread via {@link Statement#hold()} then display the script editor (in debug mode) for
 * part whose script is being executed.
 * <p>
 * Piece of cake!
 */
public class DebugContext {

    private final static DebugContext instance = new DebugContext();

    private int traceDelayMs = 500;                 // Trace delay, in milliseconds
    private boolean stepOver, stepInto, stepOut;    // Step modes
    private int debugStackDepth;                    // Last captured stack depth
    private ScriptEditor editor;                    // Editor being used as debugger UI
    private Statement debugStatement;               // Statement that caused the breakpoint
    private Thread debugThread;                     // Script execution thread we're debugging
    private ExecutionContext debugContext;          // Last captured script execution context

    // When true, a script has paused execution due to a breakpoint
    private BehaviorSubject<Boolean> isExecutionPaused = BehaviorSubject.createDefault(false);
    private BehaviorSubject<Boolean> isTracing = BehaviorSubject.createDefault(false);
    private BehaviorSubject<Boolean> isDebugging = BehaviorSubject.createDefault(false);

    private DebugContext() {
    }

    public static DebugContext getInstance() {
        return instance;
    }

    /**
     * Stop execution of the current script and begin debugging at the given statement.
     * <p>
     * Note that WyldCard supports multi-threaded script execution, but only one thread can be debugged at a time. Any
     * breakpoint reached on a different thread will be ignored while the current thread is being debugged.
     *
     * @param context   The current execution context.
     * @param statement The current statement about to be executed.
     */
    public void debug(ExecutionContext context, Statement statement) {

        // Already busy debugging another thread. 
        if (isDebugging() && !isActiveDebugThread()) {
            throw new IllegalStateException("Already debugging thread " + debugThread.getName());
        }

        // Reset step mode flags
        stepOver = false;
        stepInto = false;
        stepOut = false;

        // Capture execution state
        debugContext = context;
        debugThread = Thread.currentThread();
        debugStackDepth = context.getStackDepth();
        editor = showDebugEditor(context, context.getStackFrame().getMe());
        debugStatement = statement;

        // Notify observers
        isDebugging.onNext(true);
        isExecutionPaused.onNext(true);

        // Focus the debugger window and update the context of the variable watcher
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            WindowManager.getInstance().getVariableWatcher().setWatchedVariables(debugContext);
            WindowManager.getInstance().getExpressionEvaluator().setContext(debugContext);
            editor.getEditor().showTraceHighlight(statement.getToken().getLine() - 1);
        });

        // Special case: When tracing, delay the configured amount, remove the trace highlight and keep going.
        if (isTracing.blockingFirst()) {
            try {
                Thread.sleep(traceDelayMs);
                ThreadUtils.invokeAndWaitAsNeeded(() -> editor.getEditor().clearTraceHighlights());
            } catch (InterruptedException e) {
                // Nothing to do
            }
        }

        // Not tracing: Block the current thread; must be last thing we do
        else {
            statement.hold();
        }
    }

    /**
     * Resumes execution of the debugged thread. Has no effect if there is no thread presently being debugged.
     */
    public void resume() {
        resume(true);
    }

    /**
     * Resumes execution of the debugged thread, optionally releasing the active script editor from debug mode.
     *
     * @param releaseDebugger True to release the editor from debug mode (and clear trace highlights); false to leave
     *                        the editor in debug mode.
     */
    private void resume(boolean releaseDebugger) {
        if (isDebugging()) {
            SwingUtilities.invokeLater(() -> editor.getEditor().clearTraceHighlights());

            isExecutionPaused.onNext(false);
            debugStatement.release();

            if (releaseDebugger) {
                isDebugging.onNext(false);
                isTracing.onNext(false);

                SwingUtilities.invokeLater(() -> {
                    editor.getEditor().finishDebugging();
                    WindowManager.getInstance().getVariableWatcher().setWatchGlobalVariables();
                    WindowManager.getInstance().getExpressionEvaluator().setVisible(false);
                    clearDebugContext();
                });
            }
        }
    }

    /**
     * Toggles the state of trace mode.
     * <p>
     * When enabled, the debugged script begins executing normally, but with each statement delayed by the amount of
     * {@link #getTraceDelayMs()} and with the line of each executed statement highlighted in the debugger. This
     * feature lets a user "see" how the program is executing.
     * <p>
     * When toggled off, the debugger is released and the program executes normally.
     */
    public void toggleTrace() {
        if (isDebugging()) {
            isTracing.onNext(!isTracing.blockingFirst());

            if (isTracing.blockingFirst()) {
                resume(false);
            } else {
                resume(true);
            }
        }
    }

    /**
     * Steps out of the current handler. That is, execution is resumed until a statement in the previous stack frame
     * is reached. If there is no previous stack frame then execution is resumed normally.
     */
    public void stepOut() {
        if (isDebugging()) {
            StackFrame callingFrame = debugContext.peekStackFrame(1);
            if (callingFrame != null && isDebugging()) {
                stepOut = true;
            }

            resume(false);
        }
    }

    /**
     * Steps over the current statement. That is, the current statement is executed and the next statement in the
     * handler is debugged.
     * <p>
     * If the current statement causes the flow of execution to transfer to a different handler (for example via a
     * function call), then the script of that handler executes normally before returning to the current handler.
     * <p>
     * If there are no more statements in the current handler then execution resumes normally.
     */
    public void stepOver() {
        if (isDebugging()) {
            stepOver = true;
            resume(false);
        }
    }

    /**
     * Steps into the current statement. That is, the current statement is executed and the next statement reached by
     * this thread is debugged (irrespective of which handler or script it appears in).
     */
    public void stepInto() {
        if (isDebugging()) {
            stepInto = true;
            resume(false);
        }
    }

    /**
     * Determines if the given editor is currently debugging a script. Use {@link #isDebugging()} to detect if a script
     * is being debugged in any script editor.
     *
     * @param editor The editor to test for debug status.
     * @return True if the given editor is debugging a script.
     */
    public boolean isDebugging(ScriptEditor editor) {
        return isDebugging() && this.editor == editor;
    }

    /**
     * Determines if a script is currently being debugged.
     *
     * @return True if a script is being debugging somewhere.
     */
    public boolean isDebugging() {
        return isDebugging.blockingFirst();
    }

    /**
     * Gets an observable indicating when the debugger has paused execution of a script (i.e., a breakpoint has been
     * reached and we're waiting for user input to step or resume execution).
     *
     * @return
     */
    public Observable<Boolean> getExecutionIsPausedProvider() {
        return isExecutionPaused;
    }

    /**
     * Gets an observable of {@link #isDebugging()}.
     *
     * @return An observable indication of whether a script is currently being debugged.
     */
    public Observable<Boolean> getIsDebuggingProvider() {
        return isDebugging;
    }

    /**
     * Gets an observable indication of whether the debugger is currently tracing a script.
     *
     * @return An observable indication of tracing status.
     */
    public Observable<Boolean> getIsTracingProvider() {
        return isTracing;
    }

    /**
     * Determines if the current point of execution qualifies as a breakpoint. When a breakpoint is reached,
     * {@link #debug(ExecutionContext, Statement)} should be invoked to pause execution and open the script editor.
     * <p>
     * The method should be invoked just prior to executing the given statement.
     *
     * @param context   The current execution context
     * @param statement The current statement about to execute.
     * @return True if the current statement represents a breakpoint; false otherwise.
     */
    public boolean isBreakpoint(ExecutionContext context, Statement statement) {

        // Statements not bound to parser tokens were "synthesized" at runtime and don't qualify for step debugging
        if (statement.getToken() == null) {
            return false;
        }

        // Can only debug one thread at a time; other threads are ignored
        if (isDebugging() && !isActiveDebugThread()) {
            return false;
        }

        // Statement has a breakpoint (user marked line as breakpoint)
        if (statement.hasBreakpoint()) {
            return true;
        }

        // In trace mode, every statement is a breakpoint
        if (isTracing.blockingFirst()) {
            return true;
        }

        // Stepping over; break as long as stack depth hasn't increased
        if (stepOver && context.getStackDepth() <= debugStackDepth) {
            return true;
        }

        // Stepping out of; break as soon as current frame has popped
        if (stepOut && context.getStackDepth() < debugStackDepth) {
            return true;
        }

        // Step into; always break when in this mode
        return stepInto;
    }

    public int getTraceDelayMs() {
        return traceDelayMs;
    }

    public void setTraceDelayMs(int traceDelayMs) {
        this.traceDelayMs = traceDelayMs;
    }

    private void clearDebugContext() {
        debugContext = null;
        debugThread = null;
        debugStatement = null;
        stepOut = false;
        stepInto = false;
        stepOver = false;
    }

    private boolean isActiveDebugThread() {
        return Thread.currentThread().equals(debugThread);
    }

    private ScriptEditor showDebugEditor(ExecutionContext context, PartSpecifier partSpecifier) {
        try {
            return context.getPart(partSpecifier).editScript(context);
        } catch (PartException e) {
            throw new IllegalStateException("Bug! Attempt to debug a bogus part.");
        }
    }
}
