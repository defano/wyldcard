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
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Random;

public class RandomFunc extends ArgListFunction {

    public RandomFunc(ParserRuleContext context, Expression bound) {
        super(context, bound);
    }

    public RandomFunc(ParserRuleContext context, ExpressionList argumentList) {
        super(context, argumentList);
    }

    @Override
    public Value onEvaluate() throws HtException {

        Value boundValue = evaluateSingleArgumentList();

        if (boundValue.isNatural()) {
            return new Value(new Random().nextInt(boundValue.integerValue()));
        } else {
            throw new HtSemanticException("Random expects a non-negative integer, but got '" + boundValue.stringValue() + "'");
        }
    }
}
