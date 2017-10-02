/*
 * ChunkType
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ChunkType.java
 * @author matt.defano@gmail.com
 * 
 * Enumeration of chunk expression types
 */

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
