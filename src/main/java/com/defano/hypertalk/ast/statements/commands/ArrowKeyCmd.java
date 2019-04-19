package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.ArrowDirection;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.keyboard.RoboticTypist;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class ArrowKeyCmd extends Statement {

    private final Expression directionExpr;

    public ArrowKeyCmd(ParserRuleContext context, Expression directionExpr) {
        super(context);
        this.directionExpr = directionExpr;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        RoboticTypist.getInstance().type(ArrowDirection.fromValue(directionExpr.evaluate(context)));
    }
}
