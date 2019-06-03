package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.statement.Statement;
import com.defano.wyldcard.awt.keyboard.RoboticTypist;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class EnterKeyCmd extends Statement {

    @Inject
    private RoboticTypist roboticTypist;

    public EnterKeyCmd(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected void onExecute(ExecutionContext context) {
        roboticTypist.typeEnter(context.getCurrentCard());
    }
}
