package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.common.DateFormat;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpDateFun extends Expression {

	private final DateFormat dateFormat;

    public ExpDateFun (DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        switch (dateFormat) {
            case LONG:
                return new Value(new SimpleDateFormat("EEEEE, MMMMM dd, yyyy").format(new Date()));
            case SHORT:
                return new Value(new SimpleDateFormat("MM/dd/yy").format(new Date()));
            case ABBREVIATED:
                return new Value(new SimpleDateFormat("EEE, MMM dd, yyyy").format(new Date()));
            default:
                throw new HtSemanticException("Unsupported date format.");
        }
    }
}
