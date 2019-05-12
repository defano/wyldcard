package com.defano.hypertalk.ast.expression.operator.binary;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.operator.BinaryOperatorExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class IsNotInOp extends BinaryOperatorExp {

    public IsNotInOp(ParserRuleContext ctx, Expression lhs, Expression rhs) {
        super(ctx, lhs, rhs);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        return new Value(!rhs(context).contains(lhs(context)));
    }
}
