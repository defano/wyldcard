/*
 * ExpModifierKeyFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.gui.util.KeyboardManager;
import com.defano.hypertalk.ast.common.ModifierKey;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

public class ExpModifierKeyFun extends Expression {

    private final ModifierKey modifierKey;

    public ExpModifierKeyFun (ModifierKey modifierKey) {
        this.modifierKey = modifierKey;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        switch (modifierKey) {
            case COMMAND:
                return new Value(KeyboardManager.isCtrlCommandDown ? "down" : "up");
            case OPTION:
                return new Value(KeyboardManager.isAltOptionDown ? "down" : "up");
            case SHIFT:
                return new Value(KeyboardManager.isShiftDown ? "down" : "up");
        }

        throw new HtSemanticException("Bug! Unimplemented modifier key: " + modifierKey);
    }
}
