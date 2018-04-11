package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class GroupExp extends Expression {

    public final Expression expression;

    public GroupExp(ParserRuleContext context, Expression expression) {
        super(context);
        this.expression = expression;
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        return expression.evaluate(context);
    }
}
