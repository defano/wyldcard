package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.statement.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.keyboard.RoboticTypist;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class TabKeyCmd extends Statement {

    public TabKeyCmd(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        RoboticTypist.getInstance().type("\t");
    }
}
