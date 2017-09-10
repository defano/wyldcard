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

public class WaitCmd extends Command {

    private final Expression expression;
    private final TimeUnit units;
    private final Boolean polarity;

    public WaitCmd(Expression expression, TimeUnit units) {
        super("wait");

        this.expression = expression;
        this.units = units;
        this.polarity = null;
    }

    public WaitCmd(Expression expression, boolean polarity) {
        super("wait");

        this.expression = expression;
        this.units = null;
        this.polarity = polarity;
    }

    public void onExecute() throws HtException {

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
