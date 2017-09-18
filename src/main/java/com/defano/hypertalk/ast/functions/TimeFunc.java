/*
 * ExpTimeFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.DateFormat;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.DateUtils;

import java.util.Date;

public class TimeFunc extends Expression {

    private final DateFormat dateFormat;

    public TimeFunc(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        switch (dateFormat) {
            case LONG:
                return new Value(DateUtils.LONG_TIME.format(new Date()));
            case SHORT:
            case ABBREVIATED:
                return new Value(DateUtils.SHORT_TIME.format(new Date()));
            default:
                throw new HtSemanticException("Bug! Unimplemented time format.");
        }
    }
}
