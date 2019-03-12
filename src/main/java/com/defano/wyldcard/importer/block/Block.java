package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.ImportException;
import com.defano.wyldcard.importer.type.BlockType;
import com.defano.wyldcard.importer.result.ImportResult;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public abstract class Block {

    protected final HyperCardStack stack;
    protected final BlockType blockType;
    protected final int blockSize;
    protected final int blockId;

    public Block(HyperCardStack stack, BlockType blockType, int blockSize, int blockId) {
        this.stack = stack;
        this.blockType = blockType;
        this.blockSize = blockSize;
        this.blockId = blockId;
    }

    public abstract void deserialize(byte[] data, ImportResult report) throws ImportException;

    public String toString() {
        ToStringBuilder.setDefaultStyle(ToStringStyle.MULTI_LINE_STYLE);
        return ToStringBuilder.reflectionToString(this);
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getBlockId() {
        return blockId;
    }
}
