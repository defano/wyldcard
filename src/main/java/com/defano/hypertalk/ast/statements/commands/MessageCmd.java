package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.message.Message;
import com.defano.wyldcard.message.MessageBuilder;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.compiler.MessageCompletionObserver;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class MessageCmd extends Statement {

    private final String messageName;
    private final ListExp messageArgs;
    private PartExp messageRecipient;

    public MessageCmd(ParserRuleContext context, PartExp messageRecipient, String messageName, ListExp messageArgs) {
        super(context);
        this.messageName = messageName;
        this.messageRecipient = messageRecipient;
        this.messageArgs = messageArgs;
    }

    public MessageCmd(ParserRuleContext context, String messageName, ListExp messageArgs) {
        this(context, null, messageName, messageArgs);
    }

    public MessageCmd(ParserRuleContext context, String messageName, List<Value> messageArgs) {
        this(context, null, messageName, ListExp.fromValues(context, messageArgs.toArray(new Value[0])));
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
            Message msg = MessageBuilder.named(messageName).withArgumentExpression(context, messageArgs).build();
            recipientModel.receiveMessage(context.unbind(), msg);
        }

        // Typical case: One stack part is sending a message to another stack part
        else {
            Message msg = MessageBuilder.named(messageName).withArgumentExpression(context, messageArgs).build();
            recipientModel.receiveMessage(context.bind(recipientModel), msg);
        }
    }

    public void setMessageRecipient(PartExp messageRecipient) {
        this.messageRecipient = messageRecipient;
    }
}
