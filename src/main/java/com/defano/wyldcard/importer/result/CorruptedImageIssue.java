package com.defano.wyldcard.importer.result;

import com.defano.wyldcard.importer.type.BlockType;

public class CorruptedImageIssue extends Issue {

    public CorruptedImageIssue(int blockId) {
        super(BlockType.BMAP, blockId);
    }
}
