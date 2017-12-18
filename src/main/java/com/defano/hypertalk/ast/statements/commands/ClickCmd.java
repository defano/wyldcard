package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypercard.awt.MouseManager;
import com.defano.hypertalk.ast.model.ExpressionList;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.awt.*;

public class ClickCmd extends Command {

    private final Expression clickLoc;
    private final ExpressionList modifierKeys;

    public ClickCmd(ParserRuleContext context, Expression clickLoc) {
        super(context,"click");

        this.clickLoc = clickLoc;
        this.modifierKeys = null;
    }

    public ClickCmd(ParserRuleContext context, Expression clickLoc, ExpressionList modifierKeys) {
        super(context, "click");

        this.clickLoc = clickLoc;
        this.modifierKeys = modifierKeys;
    }

    @Override
    public void onExecute() throws HtException {
        boolean withShift = false;
        boolean withOption = false;
        boolean withCommand = false;

        if (modifierKeys != null) {
            for (Value thisModifier : modifierKeys.evaluate()) {
                withShift = thisModifier.equals(new Value("shiftKey")) || withShift;
                withOption = thisModifier.equals(new Value("optionKey")) || withOption;
                withCommand = (thisModifier.equals(new Value("commandKey")) || thisModifier.contains(new Value("cmdKey"))) || withCommand;
            }
        }

        Value clickLoc = this.clickLoc.evaluate();

        if (clickLoc.isPoint()) {
            int xLoc = clickLoc.getItems().get(0).integerValue();
            int yLoc = clickLoc.getItems().get(1).integerValue();

            MouseManager.clickAt(new Point(xLoc, yLoc), withShift, withOption, withCommand);
        } else {
            throw new HtSemanticException(clickLoc.stringValue() + " is not a valid location.");
        }
    }
}
