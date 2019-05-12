package com.defano.hypertalk.ast.expression.operator.unary;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.operator.UnaryOperatorExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class NotOp extends UnaryOperatorExp {

    public NotOp(ParserRuleContext context, Expression rhs) {
        super(context, rhs);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        return rhs(context).not();
    }
}
