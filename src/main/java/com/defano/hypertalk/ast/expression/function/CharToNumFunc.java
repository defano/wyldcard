package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class CharToNumFunc extends Expression {

    public final Expression expression;

    public CharToNumFunc(ParserRuleContext context, Expression expression) {
        super(context);
        this.expression = expression;
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        Value evaluated = expression.evaluate(context);

        if (evaluated.toString().length() == 0) {
            throw new HtSemanticException("charToNum expects a string value, but got empty.");
        }

        return new Value((int) evaluated.toString().charAt(0));
    }
}
