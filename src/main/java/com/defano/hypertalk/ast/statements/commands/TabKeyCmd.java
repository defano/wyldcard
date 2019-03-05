package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.RoboticTypist;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class TabKeyCmd extends Statement {

    public TabKeyCmd(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        RoboticTypist.getInstance().type("\t", false, false);
    }
}
