package com.defano.hypertalk.ast.model;

public enum PartType {
    FIELD("field"),
    BUTTON("button"),
    MESSAGE_BOX("message"),
    CARD("card"),
    BACKGROUND("background"),
    STACK("stack"),
    WINDOW("window"),
    HYPERCARD("hypercard");

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

    public Owner asOwner() {
        switch (this) {
            case CARD:
                return Owner.CARD;
            case BACKGROUND:
                return Owner.BACKGROUND;
            case STACK:
                return Owner.STACK;
        }

        throw new IllegalStateException("Type is not an owner: " + this);
    }

    public boolean isLayeredPart() {
        return this == BUTTON || this == FIELD;
    }
}
