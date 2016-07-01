package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;

public class ExpSecondsFun extends Expression implements Serializable {

    @Override
    public Value evaluate() throws HtSyntaxException {
        return new Value(System.currentTimeMillis() / 1000);
    }
}
