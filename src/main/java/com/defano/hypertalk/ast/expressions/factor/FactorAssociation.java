package com.defano.hypertalk.ast.expressions.factor;

import com.defano.hypertalk.ast.expressions.Expression;

/**
 * Associates an action with a given factor-expression type so that when evaluating an expression as a factor a
 * different action can be associated with different expression types (i.e., do one thing when dealing with a button,
 * do something else when dealing with a field).
 *
 * @param <ExpressionType> The type of expression this action should be associated with.
 */
public class FactorAssociation<ExpressionType extends Expression> {
    public Class<? extends Expression> expressionType;
    public FactorAction<ExpressionType> action;

    public FactorAssociation(Class<ExpressionType> expressionType, FactorAction<ExpressionType> action) {
        this.expressionType = expressionType;
        this.action = action;
    }
}
