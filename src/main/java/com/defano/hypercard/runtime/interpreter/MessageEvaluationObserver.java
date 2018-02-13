package com.defano.hypercard.runtime.interpreter;

import com.defano.hypertalk.exception.HtException;

public interface MessageEvaluationObserver {

    /**
     * Fired to indicate the message box text has been evaluated.
     * @param result The result of evaluating the message box; null if the evaluated text was a command (not an
     *               expression).
     */
    void onMessageEvaluated(String result);

    /**
     * Fired to indicate the evaluation of the message box text resulted in an error (syntax or semantic).
     * @param exception The error resulting from evaluation of the message text.
     */
    void onEvaluationError(HtException exception);
}
