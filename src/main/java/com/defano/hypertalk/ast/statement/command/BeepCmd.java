package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.statement.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.awt.*;

public class BeepCmd extends Statement {

    private final Expression beepCountExpression;

    public BeepCmd(ParserRuleContext context) {
        this(context, null);
    }

    public BeepCmd(ParserRuleContext context, Expression beepCountExpression) {
        super(context);
        this.beepCountExpression = beepCountExpression;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        int beepCount = (beepCountExpression == null) ? 1 : beepCountExpression.evaluate(context).integerValue();

        for (int count = 0; count < beepCount; count++) {
            Toolkit.getDefaultToolkit().beep();
            try {
                Thread.sleep(250);
                if (context.didAbort()) {
                    throw new HtSemanticException("Script aborted.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
