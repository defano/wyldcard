package com.defano.hypertalk.ast.model.specifiers;

public class WindowNumberSpecifier extends WindowSpecifier {

    private final int windowNumber;

    public WindowNumberSpecifier(int windowNumber) {
        this.windowNumber = windowNumber;
    }

    @Override
    public Object getValue() {
        return windowNumber;
    }
}
