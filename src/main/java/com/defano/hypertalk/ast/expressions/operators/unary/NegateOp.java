package com.defano.hypertalk.ast.expressions.operators.unary;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.operators.UnaryOperatorExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class NegateOp extends UnaryOperatorExp {

    public NegateOp(ParserRuleContext context, Expression rhs) {
        super(context, rhs);
    }

    @Override
    protected Value onEvaluate() throws HtException {
        return rhs().negate();
    }
}
