package com.defano.hypertalk.ast.expression;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * An expression with parenthesis around it, promoting its evaluation order. For example, '(3 + x)'.
 */
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
