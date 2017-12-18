package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.model.TimeUnit;
import com.defano.hypertalk.exception.HtSemanticException;
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
                Value count = expression.evaluate();

                if (count.isInteger()) {
                    Thread.sleep(units.toMilliseconds(expression.evaluate().integerValue()));
                } else {
                    throw new HtSemanticException("Expected an integer here.");
                }

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
