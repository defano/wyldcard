package com.defano.wyldcard.message;

import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.model.NamedBlock;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.awt.keyboard.DeferredKeyEvent;
import com.defano.wyldcard.parts.DeferredKeyEventListener;
import com.defano.wyldcard.runtime.executor.ScriptExecutor;
import com.defano.wyldcard.runtime.executor.observer.MessageCompletionObserver;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.ThreadChecker;

import java.awt.event.KeyEvent;
import java.util.concurrent.CountDownLatch;

/**
 * Represents an object that can receive HyperTalk messages. See {@link Message} for message structure.
 */
public interface Messagable {

    /**
     * Gets the script associated with this part.
     *
     * @param context The execution context.
     * @return The script
     */
    Script getScript(ExecutionContext context);

    /**
     * Gets a part specifier that uniquely identifies this part in the stack. This part will be bound to the 'me'
     * keyword in the script that receives messages.
     *
     * @param context The execution context.
     * @return The part specifier for the 'me' keyword.
     */
    PartSpecifier getMe(ExecutionContext context);

    /**
     * Asynchronously handles a message sent to this part's message passing hierarchy.
     * <p>
     * If an error occurs while executing the script handler associated with the message, the syntax error dialog will
     * be displayed. Note that an error received during the handling of this message does not prevent further script
     * execution.
     *
     * @param context   The execution context
     * @param initiator The statement or expression in the abstract syntax tree that caused this message to be sent, null
     *                  indicates that WyldCard generated this message.
     * @param message   The message to be passed
     */
    default void receiveMessage(ExecutionContext context, ASTNode initiator, Message message) {
        receiveMessage(context, initiator, message, (command, trapped, err) -> {
            if (err != null) {
                WyldCard.getInstance().showErrorDialog(err);
            }
        });
    }

    /**
     * Asynchronously handles a message sent by WyldCard to this part's message passing hierarchy.
     * <p>
     * This is a convenience form of {@link #receiveMessage(ExecutionContext, ASTNode, Message)} that provides a null
     * initiator (indicating that this message is the root of the call stack). Any message sent as the result
     * of executing a statement or function in a script (i.e., a function call, send command, etc.) should use
     * {@link #receiveMessage(ExecutionContext, ASTNode, Message)} and pass the statement or expression that
     * initiated the message.
     *
     * @param context The execution context
     * @param message The message to be received
     */
    default void receiveMessage(ExecutionContext context, Message message) {
        receiveMessage(context, null, message);
    }

    /**
     * Synchronously handles a message sent to this part's message passing hierarchy. This method may not be invoked on
     * the Swing dispatch thread.
     *
     * @param context   The execution context.
     * @param initiator The statement or expression in the abstract syntax tree that caused this message to be sent,
     *                  null indicates that WyldCard generated this message.
     * @param message   The message to be received
     * @throws HtException Thrown if an error occurs while handling this message.
     */
    default void blockingReceiveMessage(ExecutionContext context, ASTNode initiator, Message message) throws HtException {
        ThreadChecker.assertWorkerThread();

        CountDownLatch cdl = new CountDownLatch(1);
        final HtException[] exception = new HtException[1];

        receiveMessage(context, initiator, message, (msg, wasTrapped, error) -> {
            if (error != null) {
                exception[0] = error;
            }
            cdl.countDown();
        });

        try {
            cdl.await();
        } catch (InterruptedException e) {
            throw new HtException("Script execution interrupted.");
        }

        if (exception[0] != null) {
            throw exception[0];
        }
    }

    /**
     * Asynchronously sends a message with arguments (i.e., 'doMenu theMenu, theItem') to this part's message passing
     * hierarchy, notifying an observer with completion status when complete.
     *
     * @param context      The execution context
     * @param initiator    The statement or expression in the abstract syntax tree that caused this message to be sent,
     *                     null indicates that WyldCard generated this message.
     * @param message      The message to be received by this part
     * @param onCompletion A callback that will fire as soon as the command has been executed in script; cannot be null.
     *                     Note that this callback will not fire if the script terminates as a result of an error.
     */
    default void receiveMessage(ExecutionContext context, ASTNode initiator, Message message, MessageCompletionObserver onCompletion) {

        // No messages are sent when cmd-option is down; some messages not sent when 'lockMessages' is true
        if (WyldCard.getInstance().getKeyboardManager().isPeeking(context) ||
                (message instanceof SystemMessage &&
                        ((SystemMessage) message).isLockable() &&
                        WyldCard.getInstance().getWyldCardPart().isLockMessages())) {
            onCompletion.onMessagePassed(message, false, null);
            return;
        }

        // Attempt to invoke command handler in this part and listen for completion
        ScriptExecutor.asyncExecuteHandler(context, initiator, getMe(context), getScript(context), message, (me, script, handler, trappedMessage, exception) -> {

            // Did message generate an error
            if (exception != null) {
                onCompletion.onMessagePassed(message, true, exception);
            }

            // Did this part trap this command?
            else if (trappedMessage) {
                onCompletion.onMessagePassed(message, true, null);
            }

            // Message not trapped, send message to next part in the hierarchy
            else {
                Messagable nextRecipient = getNextMessageRecipient(context, me.getType());
                if (nextRecipient != null) {
                    nextRecipient.receiveMessage(context, initiator, message, onCompletion);
                }
            }
        });
    }

    /**
     * Consumes a {@link KeyEvent} pending the completion of a HyperTalk handler that may or may not choose to trap it.
     * <p>
     * Immediately consumes the {@link KeyEvent}, then sends a given message to this part. If the part traps the
     * message, the event is consumed, thereby preventing AWT/Swing from acting upon it. If the part does not trap the
     * HyperTalk message, a copy of the event is passed to the {@link DeferredKeyEventListener} which, in turn, can
     * pass it along to the AWT/Swing component that needs it.
     * <p>
     * This mechanism is used to prevent WyldCard fields from acting upon a keypress event until after the field's
     * script has been given a chance to trap it.
     *
     * @param context The execution context
     * @param message The message to be received
     * @param e       The input event to consume when the message is trapped by the part (or fails to invoke 'pass')
     *                within the handler.
     * @param c       The DeferredKeyEventListener to receive the KeyEvent if the script does not trap the message,
     */
    default void receiveAndDeferKeyEvent(ExecutionContext context, Message message, KeyEvent e, DeferredKeyEventListener c) {
        e.consume();

        final DeferredKeyEvent deferredCopy = new DeferredKeyEvent(
                e.getComponent(),
                e.getID(),
                e.getModifiers(),
                e.getKeyCode(),
                e.getKeyChar(),
                e.getKeyLocation());

        receiveMessage(context, null, message, (command1, wasTrapped, error) -> {
            if (!wasTrapped) {
                c.dispatchEvent(deferredCopy);
            }
        });
    }

    /**
     * Invokes a function defined in the part's script, blocking until the function completes.
     *
     * @param context   The execution context
     * @param initiator The statement or expression in the abstract syntax tree that caused this message to be sent, null
     *                  indicates that WyldCard generated this message.
     * @param message   The function to execute.
     * @return The value returned by the function upon completion.
     * @throws HtSemanticException Thrown if a syntax or semantic error occurs attempting to execute the function.
     */
    default Value invokeFunction(ExecutionContext context, ASTNode initiator, Message message) throws HtException {
        NamedBlock function = getScript(context).getNamedBlock(message.getMessageName());
        Messagable target = this;

        while (function == null) {
            // Get next part is message passing hierarchy
            target = getNextMessageRecipient(context, target.getMe(context).getType());

            // No more scripts to search; error!
            if (target == null || target == WyldCard.getInstance().getWyldCardPart()) {
                throw new HtSemanticException("No such function " + message.getMessageName() + ".");
            }

            // Look for function in this script
            function = target.getScript(context).getNamedBlock(message.getMessageName());
        }

        return ScriptExecutor.blockingExecuteFunction(context, initiator, target.getMe(context), function, message.getArguments(context));
    }

    /**
     * Gets the next part in the message passing order.
     *
     * @param context The execution context
     * @return The next messagable part in the message passing order, or null, if we've reached the last object in the
     * hierarchy.
     */
    default Messagable getNextMessageRecipient(ExecutionContext context, PartType type) {

        switch (type) {
            case BACKGROUND:
                return context.getCurrentStack().getStackModel();
            case CARD:
                return context.getCurrentCard().getPartModel().getBackgroundModel();
            case WINDOW:
            case MESSAGE_BOX:
            case FIELD:
            case BUTTON:
                return context.getCurrentCard().getPartModel();
            case STACK:
                return WyldCard.getInstance().getWyldCardPart();
            case HYPERCARD:
                return null;
        }

        throw new IllegalStateException("Bug! Unimplemented part type.");
    }
}
