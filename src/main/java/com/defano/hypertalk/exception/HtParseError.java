/*
 * HtParseError
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.exception;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Indication that a HyperTalk parse error occurred; encapsulates line and column number where the parser failed.
 */
public class HtParseError extends RuntimeException {
    public final int lineNumber, columnNumber;

    public HtParseError(ParserRuleContext ctx, String message) {
        super(message);

        this.lineNumber = ctx.getStart().getLine();
        this.columnNumber = ctx.getStart().getCharPositionInLine();
    }
}
