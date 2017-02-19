/*
 * ExpCharToNum
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

public class ExpCharToNum extends Expression {

    public final Expression expression;

    public ExpCharToNum(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        Value evaluated = expression.evaluate();

        if (evaluated.stringValue().length() == 0) {
            throw new HtSemanticException("Expected a string value here.");
        }

        return new Value((int)evaluated.stringValue().charAt(0));
    }
}
