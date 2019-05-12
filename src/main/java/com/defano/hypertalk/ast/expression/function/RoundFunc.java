package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
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
