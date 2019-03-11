package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.type.BlockType;
import com.defano.wyldcard.importer.result.Results;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public abstract class Block {

    public BlockType blockType;
    public int blockSize;
    public int blockId;

    public abstract Block deserialize(byte[] data, Results report);

    public String toString() {
        ToStringBuilder.setDefaultStyle(ToStringStyle.MULTI_LINE_STYLE);
        return ToStringBuilder.reflectionToString(this);
    }

}
