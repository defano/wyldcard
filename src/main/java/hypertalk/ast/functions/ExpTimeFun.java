package hypertalk.ast.functions;

import hypertalk.ast.common.DateFormat;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpTimeFun extends Expression implements Serializable {

    private final DateFormat dateFormat;

    public ExpTimeFun (DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public Value evaluate() throws HtSyntaxException {
        switch (dateFormat) {
            case LONG:
                return new Value(new SimpleDateFormat("h:mm:ss a").format(new Date()));
            case SHORT:
            case ABBREVIATED:
                return new Value(new SimpleDateFormat("h:mm a").format(new Date()));
            default:
                throw new HtSyntaxException("Unsupported time format.");
        }
    }
}
