package com.defano.hypertalk.ast.expressions.operators.unary;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.operators.UnaryOperatorExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class ThereIsAOp extends UnaryOperatorExp {

    public ThereIsAOp(ParserRuleContext context, Expression rhs) {
        super(context, rhs);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        return new Value(rhs.partFactor(context, PartModel.class) != null);
    }
}
