package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtSemanticException;

public class MessageCmd extends Statement {

    private final String message;
    private final ExpressionList messageArgs;

    public MessageCmd(String message, ExpressionList messageArgs) {
        this.message = message;
        this.messageArgs = messageArgs;
    }

    @Override
    public void execute() throws HtSemanticException {
        if (!ExecutionContext.getContext().hasMe()) {
            throw new HtSemanticException("Cannot send messages from here.");
        }

        try {
            ExecutionContext.getContext().sendMessage(ExecutionContext.getContext().getMe(), message, messageArgs.evaluateDisallowingCoordinates());
        } catch (Exception e) {
            HyperCard.getInstance().showErrorDialog(e);
        }
    }
}