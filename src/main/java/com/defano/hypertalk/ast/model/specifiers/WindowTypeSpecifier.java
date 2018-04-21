package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.SingletonWindowType;
import com.defano.wyldcard.window.HyperCardWindow;

public class WindowTypeSpecifier extends WindowSpecifier {

    private final SingletonWindowType windowType;

    public WindowTypeSpecifier(SingletonWindowType type) {
        this.windowType = type;
    }

    @Override
    public Object getValue() {
        return windowType.getWindow().getTitle();
    }

    public SingletonWindowType getWindowType() {
        return windowType;
    }
}
