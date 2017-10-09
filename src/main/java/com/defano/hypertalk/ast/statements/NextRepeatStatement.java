package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.breakpoints.TerminateIterationBreakpoint;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class NextRepeatStatement extends Statement {

    public NextRepeatStatement(ParserRuleContext context) {
        super(context);
    }

    @Override
    public void onExecute() throws HtException, TerminateIterationBreakpoint {
        throw new TerminateIterationBreakpoint();
    }
}
