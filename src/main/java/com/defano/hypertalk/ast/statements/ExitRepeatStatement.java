package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.preemptions.TerminateLoopPreemption;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExitRepeatStatement extends Statement {

    public ExitRepeatStatement(ParserRuleContext context) {
        super(context);
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException, TerminateLoopPreemption {
        throw new TerminateLoopPreemption();
    }
}
