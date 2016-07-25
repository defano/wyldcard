package hypertalk.ast.common;

import hypertalk.ast.expressions.Expression;

public class Destination {

    public final DestinationType type;

    public final Expression expression;
    public final Position position;
    public final Ordinal ordinal;

    public Destination(Ordinal ordinal, DestinationType type) {
        this.type = type;
        this.ordinal = ordinal;
        this.expression = null;
        this.position = null;
    }

    public Destination(Position position, DestinationType type) {
        this.type = type;
        this.position = position;
        this.expression = null;
        this.ordinal = null;
    }

    public Destination(Expression number, DestinationType type) {
        this.type = type;
        this.expression = number;
        this.position = null;
        this.ordinal = null;
    }
}
