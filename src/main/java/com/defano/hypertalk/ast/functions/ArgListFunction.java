/*
 * ArgListFunction
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.List;

public abstract class ArgListFunction extends Expression {

    private final ExpressionList argumentList;
    private final Expression expression;

    public ArgListFunction(ExpressionList argumentList) {
        this.argumentList = argumentList;
        this.expression = null;
    }

    public ArgListFunction(Expression expression) {
        this.expression = expression;
        this.argumentList = null;
    }

    public List<Value> evaluateArgumentList() throws HtSemanticException {
        if (expression != null) {
            return expression.evaluate().getItems();
        } else {
            return argumentList.evaluate();
        }
    }

    public Value evaluateSingleArgumentList() throws HtSemanticException {
        if (expression != null) {
            return expression.evaluate();
        } else {
            List<Value> evaluatedList = argumentList.evaluate();
            if (evaluatedList.size() == 1) {
                return evaluatedList.get(0);
            } else {
                throw new HtSemanticException("Expected a single argument but got " + evaluatedList.size());
            }
        }
    }

}
