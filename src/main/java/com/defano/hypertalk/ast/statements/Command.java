package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.runtime.MessageCompletionObserver;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.concurrent.CountDownLatch;

/**
 * Represents a HyperTalk command statement. HyperTalk commands are somewhat unusual in that their execution causes
 * a message to be sent the current card message hierarchy, and if the card, background or stack traps the message,
 * then the command does execute.
 */
public abstract class Command extends Statement implements MessageCompletionObserver {

    /**
     * Called to execute the command. Subclasses should implement this method as they would {@link Statement#execute()},
     * that is, this method should perform the function of the command.
     *
     * @throws HtException Thrown to indicate a syntax or semantic error occurred when executing the command.
     */
    abstract void onExecute() throws HtException;

    private final String messageName;
    private boolean trapped = false;
    private CountDownLatch cdl = new CountDownLatch(1);

    protected Command(String messageName) {
        this.messageName = messageName;
    }

    @Override
    public final void execute() throws HtException {
        cdl = new CountDownLatch(1);

        // Send command message to current card
        HyperCard.getInstance().getCard().getPartModel().receiveMessage(messageName, getEvaluatedMessageArguments(), this);

        // Wait for command handler to finish executing
        try {
            cdl.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }

        // Do no execute this command if handler trapped the message
        if (!trapped) {
            onExecute();
        }
    }

    /**
     * Gets the arguments to be passed to the command handler. Override in subclasses for commands whose associated
     * message accepts arguments.
     *
     * @return The argument list
     * @throws HtSemanticException Thrown if an error occurs evaluating arguments
     */
    protected ExpressionList getEvaluatedMessageArguments() throws HtSemanticException {
        return new ExpressionList();
    }

    /** {@inheritDoc */
    @Override
    public void onMessagePassingCompletion(String command, boolean wasTrapped, HtException err) {
        this.trapped = wasTrapped;
        cdl.countDown();
    }
}
