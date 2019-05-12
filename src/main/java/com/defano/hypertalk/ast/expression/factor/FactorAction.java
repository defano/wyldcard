package com.defano.hypertalk.ast.expression.factor;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.exception.HtException;

/**
 * An action to be taken when an expression of a given type is encountered when factoring an expression.
 * @param <ExpressionType> The type of expression this action is associated with
 */
public interface FactorAction<ExpressionType extends Expression> {

    /**
     * Invoked to trigger an action to be taken when a specific expression type is encountered as a factor.
     * @param expression The expression that was encountered when factoring.
     * @throws HtException Thrown if an error occurs processing the action.
     */
    void accept(ExpressionType expression) throws HtException;
}
