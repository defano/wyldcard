package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class MessageCmd extends Statement {

    private final String message;
    private final ListExp messageArgs;
    private PartExp messageRecipient;

    public MessageCmd(ParserRuleContext context, PartExp messageRecipient, String message, ListExp messageArgs) {
        super(context);
        this.message = message;
        this.messageRecipient = messageRecipient;
        this.messageArgs = messageArgs;
    }

    public MessageCmd(ParserRuleContext context, String message, ListExp messageArgs) {
        this(context, null, message, messageArgs);
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {

        // Who are we sending the message to?
        PartSpecifier recipient = messageRecipient == null ?
                context.getStackFrame().getMe() :
                messageRecipient.evaluateAsSpecifier(context);

        // Find the model associated with that recipient
        PartModel recipientModel = context.getPart(recipient);
        if (recipientModel == null) {
            throw new HtSemanticException("No such message recipient.");
        }

        // Special case: Message is originating from message box; use unbound context when sending
        if (recipientModel.getParentStackModel() == null) {
            recipientModel.receiveMessage(context.unbind(), message, messageArgs);
        }

        // Typical case: One stack part is sending a message to another stack part
        else {
            recipientModel.receiveMessage(context.bind(recipientModel), message, messageArgs);
        }
    }

    public void setMessageRecipient(PartExp messageRecipient) {
        this.messageRecipient = messageRecipient;
    }
}
