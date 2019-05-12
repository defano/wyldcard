package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.preemption.PassPreemption;
import com.defano.hypertalk.ast.preemption.TerminateHandlerPreemption;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.hypertalk.ast.statement.Statement;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PassCmd extends Statement {

    private final String passedMessage;

    public PassCmd(ParserRuleContext context, String passedMessage) {
        super(context);
        this.passedMessage = passedMessage;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException, TerminateHandlerPreemption {
        if (!context.getStackFrame().getMessage().equalsIgnoreCase(passedMessage)) {
            throw new HtSemanticException("Cannot pass " + passedMessage + " from within " + context.getStackFrame().getMessage());
        } else {
            throw new PassPreemption(passedMessage);
        }
    }
}
