package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.statement.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.keyboard.ModifierKey;
import com.defano.wyldcard.awt.keyboard.RoboticTypist;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class ControlKeyCmd extends Statement {

    @Inject
    private RoboticTypist roboticTypist;

    private final Expression keyExpr;

    public ControlKeyCmd(ParserRuleContext context, Expression keyExpr) {
        super(context);
        this.keyExpr = keyExpr;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        roboticTypist.type(keyExpr.evaluate(context).toString(), ModifierKey.CONTROL);
    }
}
