package hypertalk.ast.functions;

import hypertalk.ast.common.DateFormat;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;
import hypertalk.utils.DateUtils;

import java.util.Date;

public class ExpDateFun extends Expression {

    private final DateFormat dateFormat;

    public ExpDateFun (DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        return DateUtils.valueOf(new Date(), dateFormat);
    }

}
