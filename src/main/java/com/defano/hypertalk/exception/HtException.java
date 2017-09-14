/*
 * HtException
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.exception;

public class HtException extends Exception {

    public HtException(Throwable cause) {
        super(getRootCause(cause).getMessage(), cause);
    }

    public HtException(String message) {
        super(message);
    }

    public HtException(String message, Throwable cause) {
        super(message, cause);
    }

    private static Throwable getRootCause(Throwable cause) {
        if (cause.getCause() == null) {
            return cause;
        } else {
            return getRootCause(cause.getCause());
        }
    }

}
