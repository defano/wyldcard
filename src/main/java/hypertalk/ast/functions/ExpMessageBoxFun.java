package hypertalk.ast.functions;

import hypercard.HyperCard;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

public class ExpMessageBoxFun extends Expression {

    @Override
    public Value evaluate() throws HtSemanticException {
        return new Value(HyperCard.getInstance().getMsgBoxText());
    }
}
