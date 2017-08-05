package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.ExecutionContext;
import com.defano.hypertalk.ast.common.PassedCommand;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class PassCmd extends Statement {

    private final Expression passedCommand;

    public PassCmd(Expression passedCommand) {
        this.passedCommand = passedCommand;
    }

    @Override
    public void execute() throws HtException {
        String passedCommandName = passedCommand.evaluate().stringValue();
        ExecutionContext.getContext().setPassedCommand(PassedCommand.fromMessageName(passedCommandName));
        this.breakExecution = true;
    }
}
