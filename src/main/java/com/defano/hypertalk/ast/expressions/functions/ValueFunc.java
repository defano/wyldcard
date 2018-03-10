package com.defano.hypertalk.ast.expressions.functions;

import com.defano.wyldcard.runtime.interpreter.Interpreter;
import com.defano.hypertalk.ast.model.Value;
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
        return Interpreter.blockingEvaluate(toEvaluate);
    }
}
