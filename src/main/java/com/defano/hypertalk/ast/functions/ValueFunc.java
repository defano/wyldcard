package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ValueFunc extends Expression {

    public final Expression expression;

    public ValueFunc(ParserRuleContext context, Expression expression) {
        super(context);
        this.expression = expression;
    }

    @Override
    public Value onEvaluate() throws HtException {
        String toEvaluate = expression.evaluate().stringValue();
        return Interpreter.evaluate(toEvaluate);
    }
}
