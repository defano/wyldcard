/*
 * StatDragCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.gui.util.MouseManager;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

import java.awt.*;

public class DragCmd extends Statement {

    private final Expression from;
    private final Expression to;
    private final ExpressionList modifierKeys;

    public DragCmd(Expression from, Expression to) {
        this.from = from;
        this.to = to;
        this.modifierKeys = null;
    }

    public DragCmd(Expression from, Expression to, ExpressionList modifierKeys) {
        this.from = from;
        this.to = to;
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

        Value from = this.from.evaluate();
        Value to = this.to.evaluate();

        if (!from.isPoint()) {
            throw new HtSemanticException(from.stringValue() + " is not a valid location.");
        }

        if (!to.isPoint()) {
            throw new HtSemanticException(to.stringValue() + " is not a valid location.");
        }

        int x1 = from.getItems().get(0).integerValue();
        int y1 = from.getItems().get(1).integerValue();
        int x2 = to.getItems().get(0).integerValue();
        int y2 = to.getItems().get(1).integerValue();

        MouseManager.dragFrom(new Point(x1, y1), new Point(x2, y2), withShift, withOption, withCommand);
    }
}
