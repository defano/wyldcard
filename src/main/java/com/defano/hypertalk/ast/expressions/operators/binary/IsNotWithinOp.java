package com.defano.hypertalk.ast.expressions.operators.binary;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.operators.BinaryOperatorExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class IsNotWithinOp extends BinaryOperatorExp {

    public IsNotWithinOp(ParserRuleContext ctx, Expression lhs, Expression rhs) {
        super(ctx, lhs, rhs);
    }

    @Override
    protected Value onEvaluate() throws HtException {
        return lhs().within(rhs()).not();
    }
}
