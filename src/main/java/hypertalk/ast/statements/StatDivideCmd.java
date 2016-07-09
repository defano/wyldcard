package hypertalk.ast.statements;

import hypertalk.ast.containers.Destination;
import hypertalk.ast.containers.Preposition;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtException;

public class StatDivideCmd extends Statement {

    private final Expression expression;
    private final Destination destination;

    public StatDivideCmd (Expression source, Destination destination) {
        this.expression = source;
        this.destination = destination;
    }

    public void execute() throws HtException {
        destination.putValue(destination.getValue().divide(expression.evaluate()), Preposition.INTO);
    }
}
