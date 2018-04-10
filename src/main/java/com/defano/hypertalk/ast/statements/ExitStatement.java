package com.defano.hypertalk.ast.statements;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.preemptions.TerminateHandlerPreemption;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExitStatement extends Statement {

    private final String blockName;

    public ExitStatement(ParserRuleContext context, String blockName) {
        super(context);
        this.blockName = blockName;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException, TerminateHandlerPreemption {
        if (!context.getStackFrame().getMessage().equalsIgnoreCase(blockName)) {
            throw new HtSemanticException("Cannot exit '" + blockName + "' from here.");
        }

        throw new TerminateHandlerPreemption(blockName);
    }
}
