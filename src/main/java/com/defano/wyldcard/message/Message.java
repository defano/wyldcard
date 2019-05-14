package com.defano.wyldcard.message;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.List;

/**
 * Represents a HyperTalk message. Every message consists a message name (a literal value), and zero or more arguments
 * which may be HyperTalk expressions requiring evaluation.
 */
public interface Message {

    /**
     * The case-insensitive name of the message. The message name is the same as the name as the handler which will
     * service it.
     *
     * @return The name of the message.
     */
    String getMessageName();

    /**
     * Evaluates the message arguments and returns a list of {@link Value} objects representing the arguments
     * accompanying the message. Note that the number of message arguments does not have to match the number of
     * parameters defined by a message handler; extra arguments are ignored, and parameters missing arguments are bound
     * to the empty string.
     * <p>
     * Arguments are bound to parameters in left-to-right index order.
     *
     * @param context The execution context
     * @return The non-null list of arguments accompanying the message; pass an empty list for messages bearing no
     * arguments.
     */
    List<Value> evaluateArguments(ExecutionContext context) throws HtException;

    /**
     * A convenience method for converting a Message into an EvaluatedMessage.
     *
     * @param context The execution context used to evaluate the message arguments.
     * @return The evaluated message
     * @throws HtException Thrown if an error occurs while evaluating the arguments.
     */
    default EvaluatedMessage toEvaluatedMessage(ExecutionContext context) throws HtException {
        return new EvaluatedMessage(getMessageName(), evaluateArguments(context));
    }

    /**
     * Determines if the given message is considered "locked" and should not be sent.
     *
     * @return True if the 'lockMessages' attribute is true and this message is affected by that property.
     */
    default boolean isMessageLocked() {
        return this instanceof SystemMessage &&
                ((SystemMessage) this).isLockable() &&
                WyldCard.getInstance().getWyldCardPart().isLockMessages();
    }
}
