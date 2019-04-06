package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class RoundFunc extends Expression {

    private final Expression argumentExpr;

    public RoundFunc(ParserRuleContext context, Expression argumentExpr) {
        super(context);
        this.argumentExpr = argumentExpr;
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        return argumentExpr.evaluate(context).round();
    }
}
