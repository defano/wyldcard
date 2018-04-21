package com.defano.hypertalk.ast.model;

public enum PartType {
    FIELD("field"),
    BUTTON("button"),
    MESSAGE_BOX("message"),
    CARD("card"),
    BACKGROUND("background"),
    STACK("stack"),
    WINDOW("window");

    public final String hypertalkName;

    PartType(String hypertalkName) {
        this.hypertalkName = hypertalkName;
    }

    public ToolType getEditTool() {
        switch (this) {
            case BUTTON: return ToolType.BUTTON;
            case FIELD: return ToolType.FIELD;
            default: return ToolType.BROWSE;
        }
    }
}
