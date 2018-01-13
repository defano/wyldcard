package com.defano.hypercard.parts;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.runtime.MessageCompletionObserver;
import com.defano.hypercard.runtime.interpreter.Interpreter;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.expressions.functions.UserFunction;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.common.util.concurrent.CheckedFuture;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Represents an object that can receive HyperTalk messages.
 */
public interface Messagable {

    /**
     * Gets the script associated with this part.
     *
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
     *
     * @param message The message to be passed.
     */
    default void receiveMessage(String message) {
        receiveMessage(message, new ExpressionList(), (command, trapped, err) -> {
            if (err != null) {
                HyperCard.getInstance().showErrorDialog(err);
            }
        });
    }

    /**
     * Sends a message with bound arguments (i.e., 'doMenu') to this part's message passing hierarchy.
     *
     * @param message   The message to be passed
     * @param arguments The arguments to the message
     */
    default void receiveMessage(String message, ExpressionList arguments) {
        receiveMessage(message, arguments, (command, trapped, err) -> {
            if (err != null) {
                HyperCard.getInstance().showErrorDialog(err);
            }
        });
    }

    /**
     * Sends a message with arguments (i.e., 'doMenu theMenu, theItem') to this part's message passing hierarchy.
     *
     * @param command      The name of the command; cannot be null.
     * @param arguments    The arguments to pass to this command; cannot be null.
     * @param onCompletion A callback that will fire as soon as the command has been executed in script; cannot be null.
     */
    default void receiveMessage(String command, ExpressionList arguments, MessageCompletionObserver onCompletion) {

        // No commands are sent to buttons or fields when not in browse mode
        if (ToolsContext.getInstance().getToolMode() != ToolMode.BROWSE && getMe().isButtonOrFieldSpecifier()) {
            onCompletion.onMessagePassed(command, false, null);
            return;
        }

        // Attempt to invoke command handler in this part and listen for completion
        CheckedFuture<Boolean, HtException> trapped = Interpreter.executeHandler(getMe(), getScript(), command, arguments);
        trapped.addListener(() -> {
            try {

                // Did this part trap this command?
                if (trapped.checkedGet()) {
                    onCompletion.onMessagePassed(command, true, null);
                } else {
                    // Get next recipient in message passing order; null if no other parts receive message
                    Messagable nextRecipient = getNextMessageRecipient(getMe().getType());
                    if (nextRecipient == null) {
                        onCompletion.onMessagePassed(command, false, null);
                    } else {
                        nextRecipient.receiveMessage(command, arguments, onCompletion);
                    }
                }

            } catch (HtException e) {
                onCompletion.onMessagePassed(command, false, e);
            }
        }, Interpreter.getCompletionListenerExecutor());
    }

    /**
     * Sends a message to this part, and if the part (or any part in the message passing hierarchy) traps the command,
     * then the given key event is consumed ({@link InputEvent#consume()}.
     * <p>
     * In order to prevent Swing from acting on the event naturally, this method consumes the given KeyEvent and
     * re-dispatches a copy of it if this part (or any part in its message passing hierarchy) doesn't trap the message.
     * <p>
     * In order to prevent the re-dispatched event from producing a recursive call back to this method, the
     * {@link DeferredKeyEventComponent#setPendingRedispatch(boolean)} is invoked with 'true' initially, then invoked
     * with 'false' after the message has been completely received.
     *
     * @param command   The name of the command
     * @param arguments The arguments to pass to this command
     * @param e         The input event to consume if the command is trapped by the part (or fails to invoke 'pass') within
     *                  a short period of time).
     */
    default void receiveAndDeferKeyEvent(String command, ExpressionList arguments, KeyEvent e, DeferredKeyEventComponent c) {
        InputEvent eventCopy = new KeyEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar(), e.getKeyLocation());
        e.consume();

        c.setPendingRedispatch(true);
        receiveMessage(command, arguments, (command1, wasTrapped, error) -> {
            if (!wasTrapped) {
                c.dispatchEvent(eventCopy);
            }

            c.setPendingRedispatch(false);
        });
    }

    /**
     * Invokes a function defined in the part's script, blocking until the function completes.
     *
     * @param functionName The name of the function to execute.
     * @param arguments    The arguments to the function.
     * @return The value returned by the function upon completion.
     * @throws HtSemanticException Thrown if a syntax or semantic error occurs attempting to execute the function.
     */
    default Value invokeFunction(String functionName, ExpressionList arguments) throws HtException {
        UserFunction function = getScript().getFunction(functionName);
        Messagable target = this;

        while (function == null) {
            // Get next part is message passing hierarchy
            target = getNextMessageRecipient(target.getMe().getType());

            // No more scripts to search; error!
            if (target == null) {
                throw new HtSemanticException("No such function " + functionName + ".");
            }

            // Look for function in this script
            function = target.getScript().getFunction(functionName);
        }

        return Interpreter.executeFunction(target.getMe(), function, arguments);
    }

    /**
     * Gets the next part in the message passing order.
     *
     * @return The next messagable part in the message passing order, or null, if we've reached the last object in the
     * hierarchy.
     */
    default Messagable getNextMessageRecipient(PartType type) {

        switch (type) {
            case BACKGROUND:
                return HyperCard.getInstance().getStack().getStackModel();
            case MESSAGE_BOX:
                return ExecutionContext.getContext().getCurrentCard().getCardModel();
            case CARD:
                return ExecutionContext.getContext().getCurrentCard().getCardModel().getBackgroundModel();
            case STACK:
                return null;
            case FIELD:
            case BUTTON:
                if (getMe().getOwner() == Owner.BACKGROUND) {
                    return ExecutionContext.getContext().getCurrentCard().getCardModel().getBackgroundModel();
                } else {
                    return ExecutionContext.getContext().getCurrentCard().getCardModel();
                }
        }

        throw new IllegalArgumentException("Bug! Don't know what the next message recipient is for: " + getMe().getOwner());
    }

}
