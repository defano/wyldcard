package com.defano.hypertalk.ast.expressions.operators.binary;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.operators.BinaryOperatorExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class IsInOp extends BinaryOperatorExp {

    public IsInOp(ParserRuleContext ctx, Expression lhs, Expression rhs) {
        super(ctx, lhs, rhs);
    }

    @Override
    protected Value onEvaluate() throws HtException {
        return new Value(rhs().contains(lhs()));
    }
}
