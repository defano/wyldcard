package hypertalk.ast.statements;

import hypertalk.ast.containers.Destination;
import hypertalk.ast.containers.Preposition;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtException;

public class StatMultiplyCmd extends Statement {

    private final Expression expression;
    private final Destination destination;

    public StatMultiplyCmd (Expression source, Destination destination) {
        this.expression = source;
        this.destination = destination;
    }

    public void execute() throws HtException {
        destination.putValue(destination.getValue().multiply(expression.evaluate()), Preposition.INTO);
    }
}
