/*
 * HtSemanticException
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */


package com.defano.hypertalk.exception;

/**
 * Exception representing a HyperTalk semantic error. A semantic error indicates an invalid use of the language that
 * is otherwise syntactically (grammatically) correct. For example, referring to a part that does not exist, or
 * attempting to divide by zero.
 */
public class HtSemanticException extends HtException {

    public HtSemanticException(Throwable cause) {
        super(cause);
    }

    public HtSemanticException(String message) {
        super(message);
    }

    public HtSemanticException(String message, Throwable cause) {
        super(message, cause);
    }
}
