package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.preemptions.ExitToHyperCard;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExitToHyperCardStatement extends Statement {

    public ExitToHyperCardStatement(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        throw new ExitToHyperCard();
    }
}
