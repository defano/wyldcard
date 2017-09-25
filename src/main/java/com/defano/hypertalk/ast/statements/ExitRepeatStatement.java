package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.breakpoints.TerminateLoopBreakpoint;
import com.defano.hypertalk.exception.HtException;

public class ExitRepeatStatement extends Statement {

    @Override
    public void execute() throws HtException, TerminateLoopBreakpoint {
        throw new TerminateLoopBreakpoint();
    }
}
