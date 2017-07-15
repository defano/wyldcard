/*
 * HtSemanticException
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * HtSemanticException.java
 * @author matt.defano@gmail.com
 * 
 * Exception representing a HyperTalk syntax, semantic or well-formedness error
 */

package com.defano.hypertalk.exception;

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
