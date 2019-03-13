package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.ImportException;
import com.defano.wyldcard.importer.decoder.MacRomanDecoder;
import com.defano.wyldcard.importer.decoder.PatternDecoder;
import com.defano.wyldcard.importer.decoder.VersionDecoder;
import com.defano.wyldcard.importer.decoder.WOBAImageDecoder;
import com.defano.wyldcard.importer.result.ImportResult;
import com.defano.wyldcard.importer.type.BlockType;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@SuppressWarnings("unused")
public abstract class Block implements PatternDecoder, VersionDecoder, WOBAImageDecoder, MacRomanDecoder {

    // Ignore when serializing; creates cycle in object graph
    private transient final HyperCardStack stack;

    private final BlockType blockType;
    private final int blockSize;
    final int blockId;

    public Block(HyperCardStack stack, BlockType blockType, int blockSize, int blockId) {
        this.stack = stack;
        this.blockType = blockType;
        this.blockSize = blockSize;
        this.blockId = blockId;
    }

    public abstract void deserialize(byte[] data, ImportResult report) throws ImportException;

    /**
     * Gets the HyperCard stack object to which this block belongs.
     * @return The parent stack object.
     */
    public HyperCardStack getStack() {
        return stack;
    }

    /**
     * The type of block. See {@link BlockType} for details.
     * @return The block type.
     */
    public BlockType getBlockType() {
        return blockType;
    }

    /**
     * Block size including size, type and ID fields.
     * @return The total block size, in bytes.
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * The ID of this block; a unique value used to identify this block from others of the same type.
     * @return The block id
     */
    public int getBlockId() {
        return blockId;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        ToStringBuilder.setDefaultStyle(ToStringStyle.MULTI_LINE_STYLE);
        return ToStringBuilder.reflectionToString(this);
    }
}
