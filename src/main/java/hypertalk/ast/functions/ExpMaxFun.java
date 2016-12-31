package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

public class ExpMaxFun extends ArgListFunction {

    public ExpMaxFun(ArgumentList argumentList) {
        super(argumentList);
    }

    public ExpMaxFun(Expression expression) {
        super(expression);
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        Value max = new Value(Double.MIN_VALUE);

        for (Value thisValue : evaluateArgumentList()) {
            if (!thisValue.isNumber()) {
                throw new HtSemanticException("All arguments to max() must be numbers.");
            }

            if (thisValue.doubleValue() > max.doubleValue()) {
                max = thisValue;
            }
        }

        return max;
    }
}
