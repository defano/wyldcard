package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class DebugCheckpointCmd extends Statement {

    public DebugCheckpointCmd(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        // Nothing to do
    }

    @Override
    protected boolean isPermanentBreakpoint() {
        return true;
    }
}
