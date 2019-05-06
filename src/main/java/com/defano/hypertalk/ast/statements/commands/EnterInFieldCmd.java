package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.statements.Statement;
import com.defano.wyldcard.awt.keyboard.RoboticTypist;
import com.defano.wyldcard.parts.field.styles.HyperCardTextPane;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.FocusManager;
import java.awt.*;

public class EnterInFieldCmd extends Statement {

    public EnterInFieldCmd(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected void onExecute(ExecutionContext context) {
        Component focus = FocusManager.getCurrentManager().getFocusOwner();

        if (focus instanceof HyperCardTextPane) {
            RoboticTypist.getInstance().typeEnter(focus);
        }
    }
}