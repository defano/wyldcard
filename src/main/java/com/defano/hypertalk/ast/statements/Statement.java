package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.preemptions.ExitToHyperCardPreemption;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.debug.DebugContext;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.common.collect.Lists;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

public abstract class Statement extends ASTNode {

    private boolean breakpoint;         // Breakpoint applied to this line
    private CountDownLatch hold;        // Latch used to pause execution during breakpoint debugging
    private boolean abortFlag;          // Indicates script should be aborted when breakpoint is released

    public Statement(ParserRuleContext context) {
        super(context);
    }

    protected abstract void onExecute(ExecutionContext context) throws HtException, Preemption;

    public void execute(ExecutionContext context) throws HtException, Preemption {
        try {
            // Check to see if we need to break before executing this statement
            handleBreakpoints(context);

            // Delegate to the implementor
            onExecute(context);

        } catch (HtException e) {
            // Adds a breadcrumb and rethrows the exception
            rethrowContextualizedException(context, e);
        }
    }

    /**
     * Returns a non-null (but possibly empty) list of statements that appear on a given line of the script.
     * @param line The line number, counting from 1;
     * @return A list of zero or more found statements
     */
    public Collection<Statement> findStatementsOnLine(int line) {
        if (getToken().getLine() == line) {
            return Lists.newArrayList(this);
        }
        return new ArrayList<>();
    }

    public void setBreakpoint(boolean isBreakpoint) {
        this.breakpoint = isBreakpoint;
    }

    public boolean hasBreakpoint() {
        return breakpoint;
    }

    /**
     * Checks to see if the flow of execution should be stopped and the debugger invoked
     * @param context The execution context.
     */
    protected void handleBreakpoints(ExecutionContext context) {
        if (DebugContext.getInstance().isBreakpoint(context, this)) {
            DebugContext.getInstance().debug(context, this);

            if (abortFlag) {
                abortFlag = false;
                throw new ExitToHyperCardPreemption();
            }
        }
    }

    /**
     * Pauses execution of this script until {@link #release()} is invoked.
     */
    public void hold() {
        try {
            hold = new CountDownLatch(1);
            hold.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resumes execution of this script. Has no effect if the thread was not paused via a call to {@link #hold()}
     */
    public void release() {
        if (hold != null) {
            hold.countDown();
        }
    }

    /**
     * Aborts execution of this script (equivalent to invoking 'exit to hypercard').
     */
    public void abort() {
        if (hold != null) {
            abortFlag = true;
            release();
        }
    }
}
