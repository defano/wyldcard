/*
 * ExpMaxFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:12 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class MaxFunc extends ArgListFunction {

    public MaxFunc(ParserRuleContext context, ExpressionList argumentList) {
        super(context, argumentList);
    }

    public MaxFunc(ParserRuleContext context, Expression expression) {
        super(context, expression);
    }

    @Override
    public Value onEvaluate() throws HtException {
        Value max = new Value(Double.MIN_VALUE);

        for (Value thisValue : evaluateArgumentList()) {
            if (!thisValue.isNumber()) {
                throw new HtSemanticException("All arguments to max must be numbers.");
            }

            if (thisValue.doubleValue() > max.doubleValue()) {
                max = thisValue;
            }
        }

        return max;
    }
}
