package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypercard.awt.KeyboardManager;
import com.defano.hypertalk.ast.model.ModifierKey;
import com.defano.hypertalk.ast.model.Value;
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
                return new Value(KeyboardManager.getInstance().isCtrlCommandDown() ? "down" : "up");
            case OPTION:
                return new Value(KeyboardManager.getInstance().isAltOptionDown() ? "down" : "up");
            case SHIFT:
                return new Value(KeyboardManager.getInstance().isShiftDown() ? "down" : "up");
        }

        throw new HtSemanticException("Bug! Unimplemented modifier key: " + modifierKey);
    }
}
