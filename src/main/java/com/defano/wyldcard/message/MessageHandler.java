package com.defano.wyldcard.message;

import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.function.Predicate;

/**
 * A Java object acting as a handler for a message.
 */
public interface MessageHandler extends Predicate<EvaluatedMessage> {

    /**
     * Performs the action or behavior that should be taken in response to the given message.
     *
     * @param context The execution context
     * @param m The message to be handled.
     */
    void handleMessage(ExecutionContext context, EvaluatedMessage m) throws HtException;
}
