package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.keyboard.RoboticTypist;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class KeyDownCmd extends Statement {

    private final Expression keyExpr;

    public KeyDownCmd(ParserRuleContext context, Expression keyExpr) {
        super(context);
        this.keyExpr = keyExpr;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        RoboticTypist.getInstance().type(keyExpr.evaluate(context).toString());
    }
}
