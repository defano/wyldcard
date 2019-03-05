package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.preemptions.TerminateIterationPreemption;
import com.defano.wyldcard.runtime.context.ExecutionContext;
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
