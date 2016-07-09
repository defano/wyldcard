package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.io.Serializable;

public class ExpSecondsFun extends Expression implements Serializable {

	private static final long serialVersionUID = -7373748528310815582L;

	@Override
    public Value evaluate() throws HtSemanticException {
        return new Value(System.currentTimeMillis() / 1000);
    }
}
