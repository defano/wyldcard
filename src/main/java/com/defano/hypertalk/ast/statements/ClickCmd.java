/*
 * StatClickCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:12 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.gui.util.MouseManager;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

import java.awt.*;

public class ClickCmd extends Statement {

    private final Expression clickLoc;
    private final ExpressionList modifierKeys;

    public ClickCmd(Expression clickLoc) {
        this.clickLoc = clickLoc;
        this.modifierKeys = null;
    }

    public ClickCmd(Expression clickLoc, ExpressionList modifierKeys) {
        this.clickLoc = clickLoc;
        this.modifierKeys = modifierKeys;
    }

    @Override
    public void execute() throws HtSemanticException {
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
