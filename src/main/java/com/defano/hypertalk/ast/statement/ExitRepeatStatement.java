package com.defano.hypertalk.ast.statement;

import com.defano.hypertalk.ast.preemption.TerminateLoopPreemption;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExitRepeatStatement extends Statement {

    public ExitRepeatStatement(ParserRuleContext context) {
        super(context);
    }

    @Override
    public void onExecute(ExecutionContext context) throws TerminateLoopPreemption {
        throw new TerminateLoopPreemption();
    }
}
