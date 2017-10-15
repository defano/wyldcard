package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.exception.ExitToHyperCardException;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExitToHyperCardStatement extends Statement {

    public ExitToHyperCardStatement(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected void onExecute() throws HtException, Breakpoint {
        throw new ExitToHyperCardException();
    }
}
