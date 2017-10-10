package com.defano.hypertalk.exception;

/**
 * Indicates an attempt to access a property that does not exist on the requested object.
 */
public class NoSuchPropertyException extends HtSemanticException {
    public NoSuchPropertyException(String message) {
        super(message);
    }
}
