package com.defano.wyldcard.runtime.executor.observer;

import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.ast.preemption.ExitToHyperCard;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.common.util.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerExecutionFutureCallback implements FutureCallback<Boolean> {

    private static final Logger LOG = LoggerFactory.getLogger(HandlerExecutionFutureCallback.class);

    private final PartSpecifier me;
    private final Script script;
    private final String message;
    private final HandlerCompletionObserver completionObserver;

    public HandlerExecutionFutureCallback(PartSpecifier me, Script script, String message, HandlerCompletionObserver completionObserver) {
        this.completionObserver = completionObserver;
        this.me = me;
        this.script = script;
        this.message = message;
    }

    @Override
    public void onSuccess(Boolean trappedMessage) {
        completionObserver.onHandlerRan(me, script, message, trappedMessage, null);
    }

    @Override
    public void onFailure(Throwable t) {

        // Script requested termination of thread
        if (t instanceof ExitToHyperCard) {
            completionObserver.onHandlerRan(me, script, message, true, null);
        }

        // HyperTalk error occurred during execution
        else if (t instanceof HtException) {
            completionObserver.onHandlerRan(me, script, message, true, (HtException) t);
        }

        // Other error occurred that we're ill-equipped to deal with
        else {
            LOG.error("Encountered an unexpected error executing handler.", t);
            completionObserver.onHandlerRan(me, script, message, true, new HtSemanticException("Bug! An unexpected error occurred:" + t.getMessage()));
        }
    }
}
