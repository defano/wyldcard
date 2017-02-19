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
import com.defano.hypertalk.exception.HtSemanticException;

public class ExpMaxFun extends ArgListFunction {

    public ExpMaxFun(ExpressionList argumentList) {
        super(argumentList);
    }

    public ExpMaxFun(Expression expression) {
        super(expression);
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        Value max = new Value(Double.MIN_VALUE);

        for (Value thisValue : evaluateArgumentList()) {
            if (!thisValue.isNumber()) {
                throw new HtSemanticException("All arguments to max() must be numbers.");
            }

            if (thisValue.doubleValue() > max.doubleValue()) {
                max = thisValue;
            }
        }

        return max;
    }
}
