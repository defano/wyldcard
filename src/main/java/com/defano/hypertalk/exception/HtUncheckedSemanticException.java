package com.defano.hypertalk.exception;

/**
 * An unchecked exception indicating a HyperTalk semantic error was encountered inside of a Java construct or API that
 * disallows checked exceptions. For example, used to convey an error that occurs inside of a sort Comparator.
 */
public class HtUncheckedSemanticException extends RuntimeException {

    private final HtException cause;

    public HtUncheckedSemanticException(String message) {
        this(new HtException(message));
    }

    public HtUncheckedSemanticException(HtException cause) {
        super(HtException.getRootCause(cause).getMessage(), cause);
        this.cause = cause;
    }

    public HtException getHtCause() {
        return cause;
    }
}
