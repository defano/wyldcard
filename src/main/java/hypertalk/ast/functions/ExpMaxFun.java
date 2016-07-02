package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;

public class ExpMaxFun extends Expression implements Serializable {

    private final ArgumentList arguments;

    public ExpMaxFun (ArgumentList arguments) {
        this.arguments = arguments;
    }

    @Override
    public Value evaluate() throws HtSyntaxException {
        Value max = new Value(Double.MIN_VALUE);
        arguments.evaluate();

        for (Value thisValue : arguments.getEvaluatedList()) {
            if (!thisValue.isNumber()) {
                throw new HtSyntaxException("All arguments to max() must be numbers.");
            }

            if (thisValue.floatValue() > max.floatValue()) {
                max = thisValue;
            }
        }

        return max;
    }
}
