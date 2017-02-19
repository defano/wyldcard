/*
 * HtSyntaxException
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.exception;

public class HtSyntaxException extends HtException {

    public final int lineNumber, columnNumber;

    public HtSyntaxException(String message, int lineNumber, int columnNumber) {
        super(message);

        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
}
