package com.defano.hypertalk.ast.expressions.operators.unary;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.operators.UnaryOperatorExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class ThereIsNoOp extends UnaryOperatorExp {

    public ThereIsNoOp(ParserRuleContext context, Expression rhs) {
        super(context, rhs);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        return new ThereIsAOp(super.getParserContext(), rhs).evaluate(context).not();
    }
}
