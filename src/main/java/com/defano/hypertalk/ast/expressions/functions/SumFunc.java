package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.model.ExpressionList;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class SumFunc extends ArgListFunction {

    public SumFunc(ParserRuleContext context, Expression expression) {
        super(context, expression);
    }

    public SumFunc(ParserRuleContext context, ExpressionList arguments) {
        super(context, arguments);
    }


    @Override
    public Value onEvaluate() throws HtException {
        Value sum = new Value(0);

        for (Value thisValue : evaluateArgumentList()) {
            if (!thisValue.isNumber()) {
                throw new HtSemanticException("All arguments to sum must be numbers.");
            }

            sum = sum.add(thisValue);
        }

        return sum;
    }
}
