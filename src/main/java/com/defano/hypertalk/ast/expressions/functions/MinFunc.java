package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class MinFunc extends ArgListFunction {

    public MinFunc(ParserRuleContext context, Expression expression) {
        super(context, expression);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        Value min = new Value(Double.MAX_VALUE);

        List<Value> arguments = evaluateArgumentList(context);
        if (arguments.size() == 0) {
            return new Value(0);
        }

        for (Value thisValue : evaluateArgumentList(context)) {

            if (!thisValue.isNumber()) {
                throw new HtSemanticException("All arguments to min must be numbers.");
            }

            if (thisValue.doubleValue() < min.doubleValue()) {
                min = thisValue;
            }
        }

        return min;
    }
}
