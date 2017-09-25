package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.breakpoints.TerminateHandlerBreakpoint;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class ExitStatement extends Statement {

    private final String blockName;

    public ExitStatement(String blockName) {
        this.blockName = blockName;
    }

    @Override
    public void execute() throws HtException, TerminateHandlerBreakpoint {
        if (!ExecutionContext.getContext().getMessage().equalsIgnoreCase(blockName)) {
            throw new HtSemanticException("Cannot exit '" + blockName + "' from here.");
        }

        throw new TerminateHandlerBreakpoint(blockName);
    }
}
