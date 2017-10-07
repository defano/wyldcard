package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.runtime.MessageCompletionObserver;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.concurrent.CountDownLatch;

/**
 * Represents a HyperTalk command statement. HyperTalk commands are somewhat unusual in that their execution causes
 * a message to be sent the current card message hierarchy, and if the card, background or stack traps the message,
 * then the command does not execute.
 */
public abstract class Command extends Statement implements MessageCompletionObserver {

    private final String messageName;
    private boolean trapped = false;
    private CountDownLatch cdl = new CountDownLatch(1);

    protected Command(ParserRuleContext context, String messageName) {
        super(context);
        this.messageName = messageName;
    }

    @Override
    public final void execute() throws HtException, Breakpoint {
        cdl = new CountDownLatch(1);

        // Send command message to current card
        ExecutionContext.getContext().getCurrentCard().getPartModel().receiveMessage(messageName, getEvaluatedMessageArguments(), this);

        // Wait for command handler to finish executing
        try {
            cdl.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Do not execute this command if handler trapped the message
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
    public void onMessagePassed(String message, boolean wasTrapped, HtException err) {
        this.trapped = wasTrapped;
        cdl.countDown();
    }
}
