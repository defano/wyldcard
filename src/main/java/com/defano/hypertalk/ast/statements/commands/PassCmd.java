package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.preemptions.TerminateHandlerPreemption;
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
        context.setPassedMessage(passedMessage);
        throw new TerminateHandlerPreemption(passedMessage);
    }
}
