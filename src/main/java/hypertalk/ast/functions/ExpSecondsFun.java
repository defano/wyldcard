package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

public class ExpSecondsFun extends Expression {

	@Override
    public Value evaluate() throws HtSemanticException {
        return new Value(System.currentTimeMillis() / 1000);
    }
}
