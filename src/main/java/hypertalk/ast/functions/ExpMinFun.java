package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;

public class ExpMinFun extends Expression implements Serializable {

    private final Value x, y;

    public ExpMinFun (Value x, Value y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Value evaluate() throws HtSyntaxException {
        if (x.isNumber() && y.isNumber()) {
            return x.floatValue() > y.floatValue() ? y : x;
        }

        throw new HtSyntaxException("Arguments to min(x,y) must be numbers.");
    }
}
