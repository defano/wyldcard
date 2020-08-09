package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.awt.mouse.MouseManager;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

import java.awt.*;

public class ClickCmd extends Command {

    @Inject
    private MouseManager mouseManager;

    private final Expression clickLoc;
    private final Expression modifierKeys;

    public ClickCmd(ParserRuleContext context, Expression clickLoc) {
        super(context,"click");

        this.clickLoc = clickLoc;
        this.modifierKeys = null;
    }

    public ClickCmd(ParserRuleContext context, Expression clickLoc, Expression modifierKeys) {
        super(context, "click");

        this.clickLoc = clickLoc;
        this.modifierKeys = modifierKeys;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        boolean withShift = false;
        boolean withOption = false;
        boolean withCommand = false;

        if (modifierKeys != null) {
            for (Value thisModifier : modifierKeys.evaluate(context).getListItems()) {
                String modifier = thisModifier.toString().trim();

                withShift = modifier.equalsIgnoreCase("shiftKey") || withShift;
                withOption = modifier.equalsIgnoreCase("optionKey") || withOption;
                withCommand = (modifier.equalsIgnoreCase("commandKey") || modifier.equalsIgnoreCase("cmdKey")) || withCommand;
            }
        }

        Value theClickLoc = this.clickLoc.evaluate(context);

        if (theClickLoc.isPoint()) {
            int xLoc = theClickLoc.getListItems().get(0).integerValue();
            int yLoc = theClickLoc.getListItems().get(1).integerValue();

            mouseManager.clickAt(new Point(xLoc, yLoc), withShift, withOption, withCommand);
        } else {
            throw new HtSemanticException(theClickLoc.toString() + " is not a valid location.");
        }
    }
}
