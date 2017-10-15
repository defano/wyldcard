package com.defano.hypercard.runtime;

import com.defano.hypertalk.exception.ExitToHyperCardException;
import com.defano.hypertalk.exception.HtException;
import com.google.common.base.Function;

/**
 * Implements a mapping of generic exceptions to HyperTalk exceptions; required to "unwrap" the cause of exceptions
 * thrown in {@link com.google.common.util.concurrent.CheckedFuture}.
 *
 * What's the problem? When a script is executed in the background (as a result of a system message sent to a part,
 * for example) and if that script dies as a result of a syntax or semantic error, CheckedFuture will wrap the
 * HtException in something more generic (like ExecutionException) and we will loose our AST breadcrumbs that
 * identify the location and part where the error occurred.
 *
 * This class simply removes the ExecutionException wrapper.
 */
public class CheckedFutureExceptionMapper implements Function<Exception, HtException> {
    @Override
    public HtException apply(Exception input) {
        if (input instanceof HtException) {
            return (HtException) input;
        } else if (input.getCause() instanceof HtException) {
            return (HtException) input.getCause();
        } else if (input.getCause() instanceof ExitToHyperCardException) {
            throw new ExitToHyperCardException();
        }
        else {
            return new HtException("An unexpected error occurred while executing the script: " + input.getMessage());
        }
    }
}
