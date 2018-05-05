package com.defano.wyldcard.runtime.interpreter;

import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.ExitToHyperCardException;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.common.util.concurrent.FutureCallback;

public class HandlerExecutionFutureCallback implements FutureCallback<Boolean> {

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
        if (t instanceof ExitToHyperCardException) {
            completionObserver.onHandlerRan(me, script, message, true, null);
        }

        // HyperTalk error occurred during execution
        else if (t instanceof HtException) {
            completionObserver.onHandlerRan(me, script, message, true, (HtException) t);
        }

        // So other error occurred that we're ill-equipped to deal with
        else {
            completionObserver.onHandlerRan(me, script, message, true, new HtSemanticException("Bug! An unexpected error occurred:" + t.getMessage()));
        }
    }
}
