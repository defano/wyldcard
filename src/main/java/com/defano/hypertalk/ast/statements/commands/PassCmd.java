package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.preemptions.PassPreemption;
import com.defano.hypertalk.ast.preemptions.TerminateHandlerPreemption;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.statements.Statement;
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
