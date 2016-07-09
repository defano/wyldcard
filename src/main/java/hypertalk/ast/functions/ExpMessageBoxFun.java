package hypertalk.ast.functions;

import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.io.Serializable;

public class ExpMessageBoxFun extends Expression implements Serializable {

	private static final long serialVersionUID = -6803827423040799853L;

	@Override
    public Value evaluate() throws HtSemanticException {
        return new Value(RuntimeEnv.getRuntimeEnv().getMsgBoxText());
    }
}
