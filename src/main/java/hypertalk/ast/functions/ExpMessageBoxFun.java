package hypertalk.ast.functions;

import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;

public class ExpMessageBoxFun extends Expression implements Serializable {

    @Override
    public Value evaluate() throws HtSyntaxException {
        return new Value(RuntimeEnv.getRuntimeEnv().getMsgBoxText());
    }
}
