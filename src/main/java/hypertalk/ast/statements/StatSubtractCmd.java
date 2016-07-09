package hypertalk.ast.statements;

import hypertalk.ast.containers.Destination;
import hypertalk.ast.containers.Preposition;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtException;

public class StatSubtractCmd extends Statement {

    private final Expression expression;
    private final Destination destination;

    public StatSubtractCmd (Expression source, Destination destination) {
        this.expression = source;
        this.destination = destination;
    }

    public void execute() throws HtException {
        destination.putValue(destination.getValue().subtract(expression.evaluate()), Preposition.INTO);
    }
}
