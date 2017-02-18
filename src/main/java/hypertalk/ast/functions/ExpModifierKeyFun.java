/*
 * ExpModifierKeyFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/18/17 11:01 AM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package hypertalk.ast.functions;

import hypercard.gui.util.KeyboardManager;
import hypertalk.ast.common.ModifierKey;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

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
