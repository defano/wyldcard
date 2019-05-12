package com.defano.hypertalk.exception;

/**
 * Represents an error that occurs when creating, removing or modifying a part.
 */
public class HtNoSuchPartException extends HtException {
    public HtNoSuchPartException(String message) {
        super(message);
    }

    public HtNoSuchPartException(HtException cause) {
        super(cause);
    }
}
