package com.defano.wyldcard.debug;

import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.StackFrame;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.forms.ScriptEditor;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import javax.swing.*;

public class DebugContext {

    private final static DebugContext instance = new DebugContext();

    private boolean stepOver;
    private boolean stepInto;
    private boolean stepOut;

    private String debugMessage;
    private ScriptEditor editor;
    private Statement breakStatement;
    private Thread debugThread;
    private ExecutionContext debugContext;

    private BehaviorSubject<Boolean> blocked = BehaviorSubject.createDefault(false);
    private BehaviorSubject<Boolean> debuggerBusyProvider = BehaviorSubject.createDefault(false);

    private DebugContext() {
    }

    public static DebugContext getInstance() {
        return instance;
    }

    /**
     * Invoke to stop execution of the current script and begin debugging at the given statement.
     * <p>
     * Note that WyldCard supports multi-threaded script execution, but only one thread can be debugged at a time. Any
     * breakpoint reached on a different thread will be ignored while the current thread is being debugged.
     *
     * @param context   The current execution context.
     * @param statement The current statement about to be executed.
     */
    public void debug(ExecutionContext context, Statement statement) {

        // If we're already debugging another thread, ignore the breakpoint
        if (isDebugging() && !isActiveDebugThread()) {
            throw new IllegalStateException("Already debugging thread " + debugThread.getName());
        }

        stepOver = false;
        stepInto = false;
        stepOut = false;

        debugContext = context;
        debugThread = Thread.currentThread();
        editor = getDebugScriptEditor(context, context.getMe());
        breakStatement = statement;
        debugMessage = context.getMessage();

        debuggerBusyProvider.onNext(true);
        blocked.onNext(true);

        SwingUtilities.invokeLater(() -> {
            // Update variable watcher
            WindowManager.getInstance().getVariableWatcher().setWatchedVariables(debugContext.getStackFrame());
            editor.getEditor().showTraceHighlight(statement.getToken().getLine() - 1);
        });

        // Blocks the current thread; must be last thing we do
        statement.hold();
    }

    /**
     * Resumes execution of the debugged thread. Has no effect if there is no thread presently being debugged.
     */
    public void resume() {
        resume(true);
    }

    private void resume(boolean releaseDebugger) {
        if (isDebugging()) {
            SwingUtilities.invokeLater(() -> editor.getEditor().clearTraceHighlights());

            blocked.onNext(false);
            breakStatement.release();

            if (releaseDebugger) {
                debuggerBusyProvider.onNext(false);

                SwingUtilities.invokeLater(() -> {
                    editor.getEditor().finishDebugging();
                    clearDebugContext();
                });
            }
        }
    }

    /**
     * Steps out of the current handler. That is, execution is resumed until a statement in the previous stack frame
     * is reached. If there is no previous stack frame then execution is resumed normally.
     */
    public void stepOut() {
        StackFrame callingFrame = debugContext.peekStackFrame(1);
        if (callingFrame != null && isDebugging()) {
            debugMessage = callingFrame.getMessage();
            stepOut = true;
        }

        resume(false);
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
     * Determines if a script is currently being debugged (that is, script execution has been paused at a breakpoint).
     *
     * @return True if a script is being debugging somewhere.
     */
    public boolean isDebugging() {
        return debuggerBusyProvider.blockingFirst();
    }

    public Observable<Boolean> getBlockedProvider() {
        return blocked;
    }

    public Observable<Boolean> getDebuggerBusyProvider() {
        return debuggerBusyProvider;
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

        // Step over: Break on every statement in the current handler; when step-out is set, the debug message is
        // set to the last message on the call stack
        if ((stepOver || stepOut) && debugMessage.equalsIgnoreCase(context.getMessage())) {
            return true;
        }

        // Finally, always break if step into
        return stepInto;
    }

    private void clearDebugContext() {
        debugContext = null;
        debugThread = null;
        editor = null;
        breakStatement = null;
        debugMessage = null;
        stepOut = false;
        stepInto = false;
        stepOver = false;
    }

    private boolean isActiveDebugThread() {
        return Thread.currentThread().equals(debugThread);
    }

    private ScriptEditor getDebugScriptEditor(ExecutionContext context, PartSpecifier partSpecifier) {
        PartModel model = null;
        try {
            model = context.getPart(partSpecifier);
        } catch (PartException e) {
            e.printStackTrace();
        }

        ScriptEditor editor = WindowManager.getInstance().findScriptEditorForPart(model);
        if (editor != null) {
            SwingUtilities.invokeLater(() -> {
                editor.setVisible(true);
                editor.requestFocus();
            });
            return editor;
        }

        return model.editScript(context);
    }
}
