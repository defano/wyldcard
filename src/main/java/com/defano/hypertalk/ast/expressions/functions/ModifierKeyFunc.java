package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.ModifierKey;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.awt.keyboard.KeyboardManager;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class ModifierKeyFunc extends Expression {

    @Inject
    private KeyboardManager keyboardManager;

    private final ModifierKey modifierKey;

    public ModifierKeyFunc(ParserRuleContext context, ModifierKey modifierKey) {
        super(context);
        this.modifierKey = modifierKey;
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtSemanticException {
        switch (modifierKey) {
            case COMMAND:
                return new Value(keyboardManager.isCtrlCommandDown() ? "down" : "up");
            case OPTION:
                return new Value(keyboardManager.isAltOptionDown() ? "down" : "up");
            case SHIFT:
                return new Value(keyboardManager.isShiftDown() ? "down" : "up");
        }

        throw new HtSemanticException("Bug! Unimplemented modifier key: " + modifierKey);
    }
}
