package com.defano.hypertalk.ast.common;

public enum PartType {
    FIELD, BUTTON, MESSAGE_BOX, CARD, BACKGROUND, STACK;

    public ToolType getEditTool() {
        switch (this) {
            case BUTTON: return ToolType.BUTTON;
            case FIELD: return ToolType.FIELD;
            default: return ToolType.BROWSE;
        }
    }
}
