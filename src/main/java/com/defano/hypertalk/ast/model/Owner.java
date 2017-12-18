package com.defano.hypertalk.ast.model;

public enum Owner {
    BACKGROUND("Background"),
    CARD("Card"),
    STACK("Stack"),
    HYPERCARD("HyperCard");

    public final String hyperTalkName;

    Owner(String hyperTalkName) {
        this.hyperTalkName = hyperTalkName;
    }
}
