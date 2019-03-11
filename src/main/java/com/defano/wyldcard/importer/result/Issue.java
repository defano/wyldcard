package com.defano.wyldcard.importer.result;

import com.defano.wyldcard.importer.type.BlockType;

public abstract class Issue {

    private BlockType blockType;
    private int blockId;

    public Issue(BlockType blockType, int blockId) {
        this.blockType = blockType;
        this.blockId = blockId;
    }
}
