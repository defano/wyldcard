package com.defano.hypercard.parts;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.context.ToolMode;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.runtime.MessageCompletionObserver;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.Script;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.common.util.concurrent.ListenableFuture;

import java.awt.event.InputEvent;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Represents an object that can receive HyperTalk messages.
 */
public interface Messagable {

    /**
     * Gets the script associated with this part.
     * @return The script
     */
    Script getScript();

    /**
     * Gets a part specifier that uniquely identifies this part in the stack. This part will be bound to the 'me'
     * keyword in the script that receives messages.
     *
     * @return The part specifier for the 'me' keyword.
     */
    PartSpecifier getMe();

    /**
     * Sends a message (i.e., 'mouseUp') to this part's message passing hierarchy.
     * @param message The message to be passed.
     */
    default void receiveMessage(String message) {
        receiveMessage(message, new ExpressionList(), (command, trapped) -> {});
    }

    /**
     * Sends a message with bound arguments (i.e., 'doMenu') to this part's message passing hierarchy.
     * @param message The message to be passed
     * @param arguments The arguments to the message
     */
    default void receiveMessage(String message, List<Value> arguments) {
        receiveMessage(message, new ExpressionList(arguments), (command, trapped) -> {});
    }

    /**
     * Sends a message with arguments (i.e., 'doMenu theMenu, theItem') to this part's message passing hierarchy.
     *
     * @param command The name of the command; cannot be null.
     * @param arguments The arguments to pass to this command; cannot be null.
     * @param onCompletion A callback that will fire as soon as the command has been executed in script; cannot be null.
     */
    default void receiveMessage(String command, ExpressionList arguments, MessageCompletionObserver onCompletion) {

        // No commands are sent to parts when not in browse mode
        if (ToolsContext.getInstance().getToolMode() != ToolMode.BROWSE && getMe().isCardElementSpecifier()) {
            onCompletion.onMessagePassingCompletion(command, false);
            return;
        }

        try {
            // Attempt to invoke command handler in this part and listen for completion
            ListenableFuture<Boolean> trapped = Interpreter.executeCommandHandler(getMe(), getScript(), command, arguments);
            trapped.addListener(() -> {
                try {

                    // Did this part trap this command?
                    if (trapped.get()) {
                        onCompletion.onMessagePassingCompletion(command, true);
                    } else {
                        // Get next recipient in message passing order; null no other parts
                        Messagable nextRecipient = getNextMessageRecipient();
                        if (nextRecipient == null) {
                            onCompletion.onMessagePassingCompletion(command, false);
                        } else {
                            nextRecipient.receiveMessage(command, arguments, onCompletion);
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    // Thread was interrupted; nothing else we can do.
                    onCompletion.onMessagePassingCompletion(command, false);
                }
            }, Executors.newSingleThreadExecutor());
        } catch (HtSemanticException e) {
            HyperCard.getInstance().showErrorDialog(e);
        }
    }

    /**
     * Sends a message to this part, and if the part (or any part in the message passing hierarchy) traps the command,
     * then the given input event is consumed ({@link InputEvent#consume()}. This method may block the current thread
     * for a "short period of time" while the script executes.
     *
     * dispatched to a part and consumed if the part's script handles it.
     *
     * Because of Swing's threading model, we limit that semantic to guarantee that the event will be passed ONLY
     * if the handler completes quickly.
     *
     * In order to prevent Swing from acting on the event naturally, we have to block the dispatch thread while we wait
     * to see if the script is consuming the command. But the script could run indefinitely, or do something that itself
     * requires the dispatch thread. Thus, we give the script a short period of time to terminate; if it exceeds that
     * timeout the event will be consumed by the script (and not passed up the message chain) even if the script invokes
     * 'pass' after the timeout.
     *
     * @param command The name of the command
     * @param arguments The arguments to pass to this command
     * @param e The input event to consume if the command is trapped by the part (or fails to invoke 'pass') within
     *          a short period of time).
     */
    default void receiveAndConsume(String command, ExpressionList arguments, InputEvent e) {
        boolean success = receiveAndWait(command, arguments, (c, wasTrapped) -> {
            if (wasTrapped) e.consume();
        }, 500);

        if (!success) e.consume();
    }

    /**
     * Sends a message to this part's message passing hierarchy and blocks the current thread until the command
     * completes or until the timeout is reached (whichever is first). See
     * {@link #receiveMessage(String, ExpressionList, MessageCompletionObserver)} for details about sending commands.
     *
     * @param command The name of the command
     * @param arguments The arguments to pass to this command
     * @param onCompletion A callback that will fire as soon as the command has been executed in script.
     * @param timeoutMs The number of milliseconds to allow the script to execute before releasing the current thread.
     */
    default boolean receiveAndWait(String command, ExpressionList arguments, MessageCompletionObserver onCompletion, int timeoutMs) {
        CountDownLatch cdl = new CountDownLatch(1);
        receiveMessage(command, arguments, (command1, trappedInScript) -> {
            cdl.countDown();
            onCompletion.onMessagePassingCompletion(command1, trappedInScript);
        });

        try {
            return cdl.await(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * Invokes a function defined in the part's script, blocking until the function completes.
     *
     * @param function The name of the function to execute.
     * @param arguments The arguments to the function.
     * @return The value returned by the function upon completion.
     * @throws HtSemanticException Thrown if a syntax or semantic error occurs attempting to execute the function.
     */
    default Value invokeFunction(String function, ExpressionList arguments) throws HtSemanticException {
        return Interpreter.executeFunction(getMe(), getScript().getFunction(function), arguments);
    }

    /**
     * Gets the next part in the message passing order.
     * @return The next messagable part in the message passing order, or null, if we've reached the last object in the
     * hierarchy.
     */
    default Messagable getNextMessageRecipient() {

        switch (getMe().type()) {
            case BACKGROUND:
                return HyperCard.getInstance().getStack().getStackModel();
            case MESSAGE_BOX:
                return HyperCard.getInstance().getCard().getCardModel();
            case CARD:
                return HyperCard.getInstance().getCard().getCardBackground();
            case STACK:
                return null;
            case FIELD:
            case BUTTON:
                if (getMe().owner() == Owner.BACKGROUND) {
                    return HyperCard.getInstance().getCard().getCardBackground();
                } else {
                    return HyperCard.getInstance().getCard().getCardModel();
                }
        }

        throw new IllegalArgumentException("Bug! Don't know what the next message recipient is for: " + getMe().owner());
    }

}
