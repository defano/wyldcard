/*
 * ExpMinFun
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

public class MinFunc extends ArgListFunction {

    public MinFunc(Expression expression) {
        super(expression);
    }

    public MinFunc(ExpressionList arguments) {
        super(arguments);
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        Value min = new Value(Double.MAX_VALUE);

        for (Value thisValue : evaluateArgumentList()) {

            if (!thisValue.isNumber()) {
                throw new HtSemanticException("All arguments to min must be numbers.");
            }

            if (thisValue.doubleValue() < min.doubleValue()) {
                min = thisValue;
            }
        }

        return min;
    }
}
