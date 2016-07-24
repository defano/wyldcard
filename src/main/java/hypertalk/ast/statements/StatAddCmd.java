package hypertalk.ast.statements;

import hypertalk.ast.containers.Container;
import hypertalk.ast.containers.Preposition;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtException;

public class StatAddCmd extends Statement {

    private final Expression expression;
    private final Container container;

    public StatAddCmd (Expression source, Container container) {
        this.expression = source;
        this.container = container;
    }

    public void execute() throws HtException {
        container.putValue(container.getValue().add(expression.evaluate()), Preposition.INTO);
    }
}
