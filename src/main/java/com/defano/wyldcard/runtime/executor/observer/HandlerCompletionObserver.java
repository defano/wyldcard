package com.defano.wyldcard.runtime.executor.observer;

import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.exception.HtException;

public interface HandlerCompletionObserver {

    /**
     * Invoked to indicate a handler has completed execution.
     *
     * @param me The part that 'me' referred to during the execution of the handler.
     * @param script The script containing the handler
     * @param handler The name of handler that executed
     * @param trappedMessage True if the handler trapped the message, false otherwise.
     */
    void onHandlerRan(PartSpecifier me, Script script, String handler, boolean trappedMessage, HtException error);
}
