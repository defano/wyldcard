/*
 * StatWaitCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.TimeUnit;

public class StatWaitCmd extends Statement {

    private final Expression expression;
    private final TimeUnit units;
    private final Boolean polarity;

    public StatWaitCmd (Expression expression, TimeUnit units) {
        this.expression = expression;
        this.units = units;
        this.polarity = null;
    }

    public StatWaitCmd (Expression expression, boolean polarity) {
        this.expression = expression;
        this.units = null;
        this.polarity = polarity;
    }

    public void execute() throws HtException {

        if (units != null) {
            try {
                Thread.sleep(units.toMilliseconds(expression.evaluate().doubleValue()));
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
        }

        else {
            while (expression.evaluate().booleanValue() != polarity) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
        }

    }
}
