package com.defano.hypertalk.ast.statement;

import com.defano.hypertalk.ast.preemption.TerminateIterationPreemption;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class NextRepeatStatement extends Statement {

    public NextRepeatStatement(ParserRuleContext context) {
        super(context);
    }

    @Override
    public void onExecute(ExecutionContext context) throws TerminateIterationPreemption {
        throw new TerminateIterationPreemption();
    }
}
