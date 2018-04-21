package com.defano.hypertalk.ast.model.specifiers;

public class WindowNameSpecifier extends WindowSpecifier {

    private final String windowName;

    public WindowNameSpecifier(String windowName) {
        this.windowName = windowName;
    }

    @Override
    public Object getValue() {
        return windowName;
    }
}
