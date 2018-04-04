package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class MaxFunc extends ArgListFunction {

    public MaxFunc(ParserRuleContext context, Expression expression) {
        super(context, expression);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        Value max = new Value(Double.MIN_VALUE);

        for (Value thisValue : evaluateArgumentList(context)) {
            if (!thisValue.isNumber()) {
                throw new HtSemanticException("All arguments to max must be numbers.");
            }

            if (thisValue.doubleValue() > max.doubleValue()) {
                max = thisValue;
            }
        }

        return max;
    }
}
