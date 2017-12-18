package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class CharToNumFunc extends Expression {

    public final Expression expression;

    public CharToNumFunc(ParserRuleContext context, Expression expression) {
        super(context);
        this.expression = expression;
    }

    @Override
    public Value onEvaluate() throws HtException {
        Value evaluated = expression.evaluate();

        if (evaluated.stringValue().length() == 0) {
            throw new HtSemanticException("charToNum expects a string value, but got empty.");
        }

        return new Value((int)evaluated.stringValue().charAt(0));
    }
}
