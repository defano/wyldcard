package hypertalk.ast.functions;

import hypertalk.ast.common.ExpressionList;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

public class ExpMinFun extends ArgListFunction {

    public ExpMinFun (Expression expression) {
        super(expression);
    }

    public ExpMinFun (ExpressionList arguments) {
        super(arguments);
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        Value min = new Value(Double.MAX_VALUE);

        for (Value thisValue : evaluateArgumentList()) {

            if (!thisValue.isNumber()) {
                throw new HtSemanticException("All arguments to min() must be numbers.");
            }

            if (thisValue.doubleValue() < min.doubleValue()) {
                min = thisValue;
            }
        }

        return min;
    }
}
