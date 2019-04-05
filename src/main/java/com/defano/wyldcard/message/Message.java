package com.defano.wyldcard.message;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.List;

/**
 * Represents a HyperTalk message.
 */
public interface Message {

    /**
     * The case-insensitive name of the message, which is the same as the name of the handler or function which will
     * service it.
     *
     * @param context The execution context.
     * @return The name of the message.
     */
    String getMessageName(ExecutionContext context);

    /**
     * A list of {@link Value} objects representing the arguments accompanying the message. Note that the number of
     * message arguments does not have to match the number of parameters defined by a message handler; extra arguments
     * are ignored, and parameters missing arguments are bound to the empty string.
     * <p>
     * Arguments are bound to parameters in left-to-right index order.
     *
     * @param context The execution context
     * @return The non-null list of arguments accompanying the message; pass an empty list for messages bearing no
     * arguments.
     */
    List<Value> getArguments(ExecutionContext context) throws HtException;

    default String toMessageString(ExecutionContext context) throws HtException {
        StringBuilder message = new StringBuilder();
        List<Value> arguments = getArguments(context);

        message.append(getMessageName(context)).append(" ");

        for (Value argument : arguments) {
            message.append(argument).append(",");
        }

        if (!arguments.isEmpty()) {
            message.deleteCharAt(message.length() - 1);
        }

        return message.toString();
    }
}
