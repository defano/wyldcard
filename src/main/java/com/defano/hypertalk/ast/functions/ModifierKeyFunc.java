package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.awt.KeyboardManager;
import com.defano.hypertalk.ast.common.ModifierKey;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ModifierKeyFunc extends Expression {

    private final ModifierKey modifierKey;

    public ModifierKeyFunc(ParserRuleContext context, ModifierKey modifierKey) {
        super(context);
        this.modifierKey = modifierKey;
    }

    @Override
    public Value onEvaluate() throws HtSemanticException {
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
