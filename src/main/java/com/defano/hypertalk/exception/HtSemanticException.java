package com.defano.hypertalk.exception;

/**
 * Represents a HyperTalk language semantic error.
 * <p>
 * A semantic error indicates an invalid use of the language that is otherwise syntactically (grammatically) correct.
 * For example, referring to a part that does not exist, or attempting to divide by zero.
 */
public class HtSemanticException extends HtException {

    public HtSemanticException(HtException cause) {
        super(cause);
    }

    public HtSemanticException(String message) {
        super(message);
    }

    public HtSemanticException(String message, HtException cause) {
        super(message, cause);
    }
}
