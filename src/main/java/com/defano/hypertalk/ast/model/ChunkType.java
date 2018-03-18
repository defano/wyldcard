package com.defano.hypertalk.ast.model;

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

    private final String hyperTalkName;

    ChunkType(String hyperTalkName) {
        this.hyperTalkName = hyperTalkName;
    }

    public boolean isRange() {
        return this == CHARRANGE || this == WORDRANGE || this == LINERANGE || this == ITEMRANGE;
    }

    public boolean isCharChunk() {
        return this == CHAR || this == CHARRANGE;
    }

    public boolean isWordChunk() {
        return this == WORD || this == WORDRANGE;
    }

    public boolean isLineChunk() {
        return this == LINE || this == LINERANGE;
    }

    public String hyperTalkName() {
        return hyperTalkName;
    }
}
