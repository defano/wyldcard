/*
 * ExpMathFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

public class ExpMathFun extends Expression {

    public final BuiltInFunction function;
    public final Expression expression;

    public ExpMathFun(BuiltInFunction function, Expression expression) {
        this.function = function;
        this.expression = expression;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        Value operand = expression.evaluate();

        if (!operand.isNumber()) {
            throw new HtSemanticException("Function " + function + " expects a numerical operand, but got " + operand.stringValue() + " instead.");
        }

        switch (function) {
            case SQRT: return new Value(Math.sqrt(operand.doubleValue()));
            case SIN: return new Value(Math.sin(operand.doubleValue()));
            case COS: return new Value(Math.cos(operand.doubleValue()));
            case TAN: return new Value(Math.tan(operand.doubleValue()));
            case ATAN: return new Value(Math.atan(operand.doubleValue()));
            case EXP: return new Value(Math.exp(operand.doubleValue()));
            case EXP1: return new Value(Math.expm1(operand.doubleValue()));
            case EXP2: return new Value(Math.pow(2.0, operand.doubleValue()));
            case LN: return new Value(Math.log(operand.doubleValue()));
            case LN1: return new Value(Math.log1p(operand.doubleValue()));
            case LOG2: return new Value(Math.log(operand.doubleValue()) / Math.log(2.0));
            case TRUNC: return operand.trunc();
            case ABS: return operand.isInteger() ? new Value(Math.abs(operand.integerValue())) : new Value(Math.abs(operand.doubleValue()));
            case NUM_TO_CHAR: return new Value((char)operand.integerValue());
        }

        throw new IllegalArgumentException("Bug! Unimplemented mathematical function: " + function);
    }
}
