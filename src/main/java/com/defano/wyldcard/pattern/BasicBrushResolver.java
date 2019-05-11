package com.defano.wyldcard.pattern;

import com.defano.wyldcard.paint.PaintBrush;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.Arrays;

public class BasicBrushResolver {

    public static Value valueOfBasicBrush(PaintBrush brush) {
        return new Value(Arrays.asList(PaintBrush.values()).indexOf(brush));
    }

    public static PaintBrush basicBrushOfValue(Value value) throws HtSemanticException {
        if (!value.isInteger() || value.integerValue() < 0 || value.integerValue() >= PaintBrush.values().length) {
            throw new HtSemanticException("Not a valid brush number.");
        }

        return PaintBrush.values()[value.integerValue()];
    }
}
