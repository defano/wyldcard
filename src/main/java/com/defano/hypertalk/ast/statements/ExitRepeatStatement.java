package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.breakpoints.TerminateLoopBreakpoint;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExitRepeatStatement extends Statement {

    public ExitRepeatStatement(ParserRuleContext context) {
        super(context);
    }

    @Override
    public void onExecute() throws HtException, TerminateLoopBreakpoint {
        throw new TerminateLoopBreakpoint();
    }
}
