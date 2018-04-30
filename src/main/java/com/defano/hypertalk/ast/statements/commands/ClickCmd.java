package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.awt.MouseManager;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.awt.*;

public class ClickCmd extends Command {

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
            for (Value thisModifier : modifierKeys.evaluate(context).getItems(context)) {
                withShift = thisModifier.equals(new Value("shiftKey")) || withShift;
                withOption = thisModifier.equals(new Value("optionKey")) || withOption;
                withCommand = (thisModifier.equals(new Value("commandKey")) || thisModifier.contains(new Value("cmdKey"))) || withCommand;
            }
        }

        Value clickLoc = this.clickLoc.evaluate(context);

        if (clickLoc.isPoint()) {
            int xLoc = clickLoc.getItems(context).get(0).integerValue();
            int yLoc = clickLoc.getItems(context).get(1).integerValue();

            MouseManager.getInstance().clickAt(new Point(xLoc, yLoc), withShift, withOption, withCommand);
        } else {
            throw new HtSemanticException(clickLoc.stringValue() + " is not a valid location.");
        }
    }
}
