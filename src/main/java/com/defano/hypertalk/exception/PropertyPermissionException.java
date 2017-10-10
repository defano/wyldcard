package com.defano.hypertalk.exception;

/**
 * Indicates an attempt to write a property that is read-only.
 */
public class PropertyPermissionException extends HtSemanticException {

    public PropertyPermissionException (String message) {
        super(message);
    }
}
