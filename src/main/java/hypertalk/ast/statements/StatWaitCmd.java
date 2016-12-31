package hypertalk.ast.statements;

import hypertalk.ast.common.TimeUnit;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtException;

public class StatWaitCmd extends Statement {

    private final Expression expression;
    private final TimeUnit units;
    private final Boolean polarity;

    public StatWaitCmd (Expression expression, TimeUnit units) {
        this.expression = expression;
        this.units = units;
        this.polarity = null;
    }

    public StatWaitCmd (Expression expression, boolean polarity) {
        this.expression = expression;
        this.units = null;
        this.polarity = polarity;
    }

    public void execute() throws HtException {

        if (units != null) {
            try {
                Thread.sleep(units.toMilliseconds(expression.evaluate().doubleValue()));
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
        }

        else {
            while (expression.evaluate().booleanValue() != polarity) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
        }

    }
}
