package com.defano.hypertalk.ast.common;

public enum Owner {
    BACKGROUND("Background"),
    CARD("Card"),
    STACK("Stack"),
    HYPERCARD("HyperCard");

    public final String friendlyName;

    Owner(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
