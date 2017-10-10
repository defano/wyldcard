package com.defano.hypertalk.ast.common;

public enum ChunkType
{
    CHAR("char"),
    WORD("word"),
    ITEM("item"),
    LINE("line"),
    CHARRANGE("chars"),
    WORDRANGE("words"),
    LINERANGE("lines"),
    ITEMRANGE("items");

    private final String friendlyName;

    ChunkType(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public boolean isRange() {
        return this == CHARRANGE || this == WORDRANGE || this == LINERANGE || this == ITEMRANGE;
    }

    public String friendlyName() {
        return friendlyName;
    }
}
