package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
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
    public void onExecute() throws HtSemanticException {
        if (!ExecutionContext.getContext().hasMe()) {
            throw new HtSemanticException("Cannot send messages from here.");
        }

        try {
            ExecutionContext.getContext().sendMessage(ExecutionContext.getContext().getMe(), message, messageArgs);
        } catch (HtException e) {
            HyperCard.getInstance().showErrorDialog(e);
        }
    }
}
