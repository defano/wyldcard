package com.defano.hypercard.parts;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.context.ToolMode;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.runtime.CommandCompletionHandler;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.*;
import com.defano.hypertalk.ast.containers.PartIdSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.common.util.concurrent.ListenableFuture;

import java.awt.event.InputEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Represents an object that can receive HyperTalk messages.
 */
public interface Messagable {

    Owner getOwner();
    PartType getType();
    int getId();
    Script getScript();

    /**
     * Sends a message (i.e., 'mouseUp') to this part.
     * @param message The message to be passed.
     */
    default void sendMessage(String message) {
        if (ToolsContext.getInstance().getToolMode() == ToolMode.BROWSE) {
            Interpreter.executeHandler(new PartIdSpecifier(getOwner(), getType(), getId()), getScript(), message);
        }
    }

    /**
     * Sends a command (i.e., 'doMenu') to this part.
     * <p>
     * A command is special type of handler enabling a part to override
     * certain HyperCard UI behaviors. If the part does not handle the command (that is, the script does not contain a
     * handler matching its name), or, if it passes the command inside that script (i.e., 'pass keyDown'), then the
     * message is sent to the next part in the message passing order. This process repeats until a script traps the
     * command or it reaches HyperCard (the end of the passing hierarchy).
     * <p>
     * Only HyperCard can originate commands. There is no HyperTalk mechanism to send a command to a part.
     *
     * @param command The name of the command
     * @param arguments The arguments to pass to this command
     * @param onCompletion A callback that will fire as soon as the command has been executed in script.
     */
    default void sendCommand(final PassedCommand command, final ExpressionList arguments, final CommandCompletionHandler onCompletion) {

        // No commands are sent to parts when not in browse mode
        if (ToolsContext.getInstance().getToolMode() != ToolMode.BROWSE) {
            onCompletion.onCommandCompleted(command, false);
            return;
        }

        // Attempt to invoke command handler in this part and listen for completion
        ListenableFuture<Boolean> trapped = Interpreter.executeCommandHandler(new PartIdSpecifier(getOwner(), getType(), getId()), getScript(), command, arguments);
        trapped.addListener(() -> {
            try {

                // Did this part trap this command?
                if (trapped.get()) {
                    onCompletion.onCommandCompleted(command, true);
                } else {
                    // Get next recipient in message passing order; null no other parts
                    Messagable nextRecipient = getNextMessageRecipient();
                    if (nextRecipient == null) {
                        onCompletion.onCommandCompleted(command, false);
                    } else {
                        nextRecipient.sendCommand(command, arguments, onCompletion);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                // Thread was interrupted; nothing else we can do.
                onCompletion.onCommandCompleted(command, false);
            }
        }, Executors.newSingleThreadExecutor());
    }

    /**
     * Sends a command to this part, and if the part (or any part in the message passing hierarchy) traps the command,
     * then the given input event is consumed ({@link InputEvent#consume()}. This method may block the current thread
     * for a "short period of time" while the script executes.
     *
     * The intent of this method is to be called on the dispatch thread when an AWT event is received that should be
     * dispatched to a part and consumed if the part's script handles it.
     *
     * Because of Swing's threading model, we limit that semantic to guarantee that the event will be passed ONLY
     * if the handler completes quickly.
     *
     * The issue here is this: In order to prevent Swing from acting on the event
     * naturally, we have to block the dispatch thread while we wait to see if the script is consuming the command.
     * But the script could run indefinitely, or do something that itself requires the dispatch thread. Thus, we
     * give the script a short period of time to terminate; if it exceeds that timeout the event will be consumed
     * by the script (and not passed up the message chain) even if the script invokes 'pass' after the timeout.
     *
     * @param command The name of the command
     * @param arguments The arguments to pass to this command
     * @param e The input event to consume if the command is trapped by the part (or fails to invoke 'pass' within
     *          a short period of time).
     */
    default void sendAndConsume(PassedCommand command, ExpressionList arguments, InputEvent e) {
        boolean success = blockingSendCommand(command, arguments, (c, trappedInScript) -> {
            if (trappedInScript) e.consume();
        }, 500);

        if (!success) e.consume();
    }

    /**
     * Sends a command to this part's message passing hierarchy, and blocks the current thread until the command
     * completes or the timeout is reached (whichever is first). See
     * {@link #sendCommand(PassedCommand, ExpressionList, CommandCompletionHandler)} for details about sending commands.
     *
     * @param command The name of the command
     * @param arguments The arguments to pass to this command
     * @param onCompletion A callback that will fire as soon as the command has been executed in script.
     * @param timeoutMs The number of milliseconds to allow the script to execute before releasing the current thread.
     */
    default boolean blockingSendCommand(PassedCommand command, ExpressionList arguments, CommandCompletionHandler onCompletion, int timeoutMs) {
        CountDownLatch cdl = new CountDownLatch(1);
        sendCommand(command, arguments, (command1, trappedInScript) -> {
            cdl.countDown();
            onCompletion.onCommandCompleted(command1, trappedInScript);
        });

        try {
            return cdl.await(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * Attempts to execute a function defined in the part's script.
     * @param function The name of the function to execute.
     * @param arguments The arguments to the function.
     * @return The value returned by the function upon completion.
     * @throws HtSemanticException Thrown if a syntax or semantic error occurs attempting to execute the function.
     */
    default Value executeUserFunction(String function, ExpressionList arguments) throws HtSemanticException {
        return Interpreter.executeFunction(new PartIdSpecifier(getOwner(), getType(), getId()), getScript().getFunction(function), arguments);
    }

    /**
     * Gets the next part in the message passing order.
     * @return The next messagable part in the message passing order, or null, if we've reached the last object in the
     * hierarchy.
     */
    default Messagable getNextMessageRecipient() {
        switch (getType()) {
            case BACKGROUND:
                return HyperCard.getInstance().getStack().getStackModel();
            case FIELD:
            case BUTTON:
            case MESSAGEBOX:
                return HyperCard.getInstance().getCard().getCardModel();
            case CARD:
                return HyperCard.getInstance().getCard().getCardBackground();
            case STACK:
                return null;
        }

        throw new IllegalArgumentException("Bug! Don't know what the next recipient is for: " + getOwner());
    }
}
