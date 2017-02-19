/*
 * ExpValueFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

public class ExpValueFun extends Expression {

    public final Expression expression;

    public ExpValueFun(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        String toEvaluate = expression.evaluate().stringValue();
        return Interpreter.evaluate(toEvaluate);
    }
}
