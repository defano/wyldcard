package com.defano.hypercard.patterns;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.jmonet.tools.brushes.BasicBrush;

import java.util.Arrays;

public class BasicBrushResolver {

    public static Value valueOfBasicBrush(BasicBrush brush) {
        return new Value(Arrays.asList(BasicBrush.values()).indexOf(brush));
    }

    public static BasicBrush basicBrushOfValue(Value value) throws HtSemanticException {
        if (!value.isInteger() || value.integerValue() < 0 || value.integerValue() >= BasicBrush.values().length) {
            throw new HtSemanticException("Not a valid brush number.");
        }

        return BasicBrush.values()[value.integerValue()];
    }
}
