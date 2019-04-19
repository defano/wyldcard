package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.statements.Statement;
import com.defano.wyldcard.awt.keyboard.RoboticTypist;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class EnterKeyCmd extends Statement {

    public EnterKeyCmd(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected void onExecute(ExecutionContext context) {
        RoboticTypist.getInstance().typeEnter(context.getCurrentCard());
    }
}
