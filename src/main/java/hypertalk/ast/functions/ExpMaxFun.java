package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.io.Serializable;

public class ExpMaxFun extends Expression implements Serializable {

	private static final long serialVersionUID = 8135133323812108442L;
	private final ArgumentList arguments;

    public ExpMaxFun (ArgumentList arguments) {
        this.arguments = arguments;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        Value max = new Value(Double.MIN_VALUE);
        arguments.evaluate();

        for (Value thisValue : arguments.getEvaluatedList()) {
            if (!thisValue.isNumber()) {
                throw new HtSemanticException("All arguments to max() must be numbers.");
            }

            if (thisValue.floatValue() > max.floatValue()) {
                max = thisValue;
            }
        }

        return max;
    }
}
