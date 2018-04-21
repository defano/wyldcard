package com.defano.hypertalk.ast.model.specifiers;

public class WindowIdSpecifier extends WindowSpecifier {

    private final int id;

    public WindowIdSpecifier(int id) {
        this.id = id;
    }

    @Override
    public Object getValue() {
        return id;
    }
}
