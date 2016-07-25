package hypertalk.ast.statements;

import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.common.Destination;
import hypertalk.ast.common.Ordinal;
import hypertalk.ast.common.Position;
import hypertalk.exception.HtException;

public class StatGoCmd extends Statement {

    private final Destination destination;

    public StatGoCmd (Destination destination) {
        this.destination = destination;
    }

    public void execute() throws HtException {
        if (destination.ordinal != null) {
            if (destination.ordinal == Ordinal.FIRST) {
                RuntimeEnv.getRuntimeEnv().getStack().goFirstCard();
            } else if (destination.ordinal == Ordinal.LAST) {
                RuntimeEnv.getRuntimeEnv().getStack().goLastCard();
            } else {
                RuntimeEnv.getRuntimeEnv().getStack().goCard(destination.ordinal.intValue() - 1);
            }
        }

        else if (destination.position != null) {
            if (destination.position == Position.NEXT) {
                RuntimeEnv.getRuntimeEnv().getStack().goNextCard();
            } else {
                RuntimeEnv.getRuntimeEnv().getStack().goPrevCard();
            }
        }

        else if (destination.expression != null) {
            RuntimeEnv.getRuntimeEnv().getStack().goCard(destination.expression.evaluate().integerValue() - 1);
        }
    }
}
