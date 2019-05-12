package com.defano.hypertalk.exception;

/**
 * Indicates an attempt to access a property that does not exist on the requested object.
 */
public class HtNoSuchPropertyException extends HtSemanticException {
    public HtNoSuchPropertyException(String message) {
        super(message);
    }
}
