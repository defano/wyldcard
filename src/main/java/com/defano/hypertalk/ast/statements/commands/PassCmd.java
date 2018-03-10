package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.breakpoints.TerminateHandlerBreakpoint;
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
    public void onExecute() throws HtException, TerminateHandlerBreakpoint {
        ExecutionContext.getContext().setPassedMessage(passedMessage);
        throw new TerminateHandlerBreakpoint(passedMessage);
    }
}
