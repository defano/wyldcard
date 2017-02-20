/*
 * ExpRandomFun
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

import java.util.Random;

public class ExpRandomFun extends ArgListFunction {

    public ExpRandomFun(Expression bound) {
        super(bound);
    }

    public ExpRandomFun(ExpressionList argumentList) {
        super(argumentList);
    }

    @Override
    public Value evaluate() throws HtSemanticException {

        Value boundValue = evaluateSingleArgumentList();

        if (boundValue.isNatural()) {
            return new Value(new Random().nextInt(boundValue.integerValue()));
        } else {
            throw new HtSemanticException("Random expects a non-negative integer, but got: " + boundValue.stringValue());
        }
    }
}
