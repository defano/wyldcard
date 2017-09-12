package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

public class SumFunc extends ArgListFunction {

    public SumFunc(Expression expression) {
        super(expression);
    }

    public SumFunc(ExpressionList arguments) {
        super(arguments);
    }


    @Override
    public Value evaluate() throws HtSemanticException {
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
