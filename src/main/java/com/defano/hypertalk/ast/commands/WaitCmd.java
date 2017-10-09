/*
 * StatWaitCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.TimeUnit;
import org.antlr.v4.runtime.ParserRuleContext;

public class WaitCmd extends Command {

    private final Expression expression;
    private final TimeUnit units;
    private final Boolean polarity;

    public WaitCmd(ParserRuleContext context, Expression expression, TimeUnit units) {
        super(context, "wait");

        this.expression = expression;
        this.units = units;
        this.polarity = null;
    }

    public WaitCmd(ParserRuleContext context, Expression expression, boolean polarity) {
        super(context, "wait");

        this.expression = expression;
        this.units = null;
        this.polarity = polarity;
    }

    public void onExecute() throws HtException {

        if (units != null) {
            try {
                Thread.sleep(units.toMilliseconds(expression.evaluate().doubleValue()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        else {
            while (expression.evaluate().booleanValue() != polarity) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

    }
}
