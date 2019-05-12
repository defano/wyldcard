package com.defano.wyldcard.stackreader.record;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@SuppressWarnings("unused")
public class BlockOffsetRecord {

    private int blockOffset;
    private byte blockId;

    public BlockOffsetRecord(int blockOffset, byte blockId) {
        this.blockOffset = blockOffset;
        this.blockId = blockId;
    }

    public int getBlockOffset() {
        return blockOffset;
    }

    public byte getBlockId() {
        return blockId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
