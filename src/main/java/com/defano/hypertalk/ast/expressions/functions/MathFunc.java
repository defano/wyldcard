package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.model.BuiltInFunction;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class MathFunc extends Expression {

    public final BuiltInFunction function;
    public final Expression expression;

    public MathFunc(ParserRuleContext context, BuiltInFunction function, Expression expression) {
        super(context);
        this.function = function;
        this.expression = expression;
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        Value operand = expression.evaluate(context);

        if (!operand.isNumber()) {
            throw new HtSemanticException("Function " + function + " expects a numerical operand, but got " + operand.toString() + " instead.");
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
