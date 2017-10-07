/*
 * HtSyntaxException
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.exception;

import com.defano.hypertalk.utils.Range;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

public class HtSyntaxException extends HtException {

    private final Token offendingToken;

    public HtSyntaxException(RecognitionException e) {
        super(getFriendlyMessage(e.getOffendingToken()));
        this.offendingToken = e.getOffendingToken();
    }

    public HtSyntaxException(Token offendingToken) {
        super(getFriendlyMessage(offendingToken));
        this.offendingToken = offendingToken;
    }

    public Token getOffendingToken() {
        return offendingToken;
    }

    public Range getOffendingRange() {
        int start = getOffendingToken().getStartIndex();
        int end = getOffendingToken().getStopIndex();

        if (end > start) {
            return new Range(start, end + 1);
        }

        return null;
    }

    private static String getFriendlyMessage(Token t) {
        return "Don't understand '" + t.getText() + "' on line " + t.getLine() + ", column " + t.getCharPositionInLine() + ".";
    }
}
