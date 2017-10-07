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
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public abstract class ArgListFunction extends Expression {

    private final ExpressionList argumentList;
    private final Expression expression;

    public ArgListFunction(ParserRuleContext context, ExpressionList argumentList) {
        super(context);
        this.argumentList = argumentList;
        this.expression = null;
    }

    public ArgListFunction(ParserRuleContext context, Expression singleArgument) {
        super(context);
        this.expression = singleArgument;
        this.argumentList = null;
    }

    /**
     * Evaluates this list of argument expressions and returns a list of values.
     *
     * When constructed with a single argument Expression, this method returns a single-item list containing the
     * evaluated result of the single argument expression.
     *
     * When constructed with an ExpressionList, this method performing a "diving evaluation" of each expression in
     * the ExpressionList and returns a list of Values the size of which is equal to the number of items appearing
     * in the ExpressionList. Note that this is not necessarily the same as the length of the ExpressionList itself;
     * this method attempts to pull apart any single argument into a sublist of arguments.
     *
     * For example, (1, 2, 3) results in three values '1', '2', '3'); and so does (1, "2, 3").
     *
     * @return A list of evaluated arguments passed to the arg list function.
     * @throws HtSemanticException If an error occurs evaluating the expressions.
     */
    public List<Value> evaluateArgumentList() throws HtSemanticException {
        if (expression != null) {
            return expression.evaluate().getItems();
        } else {
            return argumentList.evaluateDisallowingCoordinates();
        }
    }

    /**
     * Assumes the argument list contains only a single argument and returns the evaluation of it; produces a semantic
     * error if the number of arguments is not 1.
     *
     * @return The evaluated singleton argument value.
     * @throws HtSemanticException If an error occurs evaluating the expressions.
     */
    public Value evaluateSingleArgumentList() throws HtSemanticException {
        if (expression != null) {
            return expression.evaluate();
        } else {
            List<Value> evaluatedList = argumentList.evaluate();
            if (evaluatedList.size() == 1) {
                return evaluatedList.get(0);
            } else {
                throw new HtSemanticException("Expected a single argument, but got " + evaluatedList.size() + " arguments.");
            }
        }
    }

}
