package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class PassCmd extends Statement {

    private final Expression passedMessage;

    public PassCmd(Expression passedMessage) {
        this.passedMessage = passedMessage;
    }

    @Override
    public void execute() throws HtException {
        ExecutionContext.getContext().setPassedMessage(passedMessage.evaluate().stringValue());
        this.breakExecution = true;
    }
}
