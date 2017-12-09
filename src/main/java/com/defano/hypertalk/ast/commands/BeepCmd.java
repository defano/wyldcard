package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.awt.KeyboardManager;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.awt.*;

public class BeepCmd extends Command {

    private final Expression beepCountExpression;

    public BeepCmd(ParserRuleContext context) {
        this(context, null);
    }

    public BeepCmd(ParserRuleContext context, Expression beepCountExpression) {
        super(context, "beep");
        this.beepCountExpression = beepCountExpression;
    }

    @Override
    public void onExecute() throws HtException {
        int beepCount = (beepCountExpression == null) ? 1 : beepCountExpression.evaluate().integerValue();

        for (int count = 0; count < beepCount; count++) {
            Toolkit.getDefaultToolkit().beep();
            try {
                Thread.sleep(250);
                if (KeyboardManager.isBreakSequence) {
                    throw new HtSemanticException("Script aborted.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
