package hypertalk.ast.statements;

import hypertalk.ast.containers.Container;
import hypertalk.ast.containers.Preposition;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtException;

public class StatSubtractCmd extends Statement {

    private final Expression expression;
    private final Container container;

    public StatSubtractCmd (Expression source, Container container) {
        this.expression = source;
        this.container = container;
    }

    public void execute() throws HtException {
        container.putValue(container.getValue().subtract(expression.evaluate()), Preposition.INTO);
    }
}
