package com.defano.wyldcard.importer.result;

import com.defano.wyldcard.importer.type.BlockType;

public class MalformedBlockIssue extends Issue {

    public MalformedBlockIssue(BlockType blockType, int blockId) {
        super(blockType, blockId);
    }
}
