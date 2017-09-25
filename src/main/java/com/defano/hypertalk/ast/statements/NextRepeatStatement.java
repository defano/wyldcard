package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.breakpoints.TerminateIterationBreakpoint;
import com.defano.hypertalk.exception.HtException;

public class NextRepeatStatement extends Statement {

    @Override
    public void execute() throws HtException, TerminateIterationBreakpoint {
        throw new TerminateIterationBreakpoint();
    }
}
