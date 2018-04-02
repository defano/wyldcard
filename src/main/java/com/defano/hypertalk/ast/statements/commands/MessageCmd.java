package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class MessageCmd extends Statement {

    private final String message;
    private final ListExp messageArgs;

    public MessageCmd(ParserRuleContext context, String message, ListExp messageArgs) {
        super(context);
        this.message = message;
        this.messageArgs = messageArgs;
    }

    @Override
    public void onExecute() throws HtException {
        ExecutionContext.getContext().sendMessage(ExecutionContext.getContext().getMe(), message, messageArgs);
    }
}
