package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;

public class ExpMinFun extends Expression implements Serializable {

    private final ArgumentList arguments;

    public ExpMinFun (ArgumentList arguments) {
        this.arguments = arguments;
    }

    @Override
    public Value evaluate() throws HtSyntaxException {
        Value min = new Value(Double.MAX_VALUE);
        arguments.evaluate();

        for (Value thisValue : arguments.getEvaluatedList()) {

            if (!thisValue.isNumber()) {
                throw new HtSyntaxException("All arguments to min() must be numbers.");
            }

            if (thisValue.floatValue() < min.floatValue()) {
                min = thisValue;
            }
        }

        return min;
    }
}
