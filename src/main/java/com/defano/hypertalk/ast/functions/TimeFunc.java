/*
 * ExpTimeFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.ConvertibleDateFormat;
import com.defano.hypertalk.ast.common.DateLength;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Date;

public class TimeFunc extends Expression {

    private final DateLength dateLength;

    public TimeFunc(ParserRuleContext context, DateLength dateLength) {
        super(context);
        this.dateLength = dateLength;
    }

    @Override
    public Value onEvaluate() throws HtSemanticException {
        switch (dateLength) {
            case LONG:
                return new Value(ConvertibleDateFormat.LONG_TIME.dateFormat.format(new Date()));
            case SHORT:
            case ABBREVIATED:
                return new Value(ConvertibleDateFormat.SHORT_TIME.dateFormat.format(new Date()));
            default:
                throw new HtSemanticException("Bug! Unimplemented time format.");
        }
    }
}
