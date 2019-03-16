package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.misc.ImportException;
import com.defano.wyldcard.importer.decoder.MacRomanDecoder;
import com.defano.wyldcard.importer.decoder.PatternDecoder;
import com.defano.wyldcard.importer.decoder.VersionDecoder;
import com.defano.wyldcard.importer.decoder.WOBAImageDecoder;
import com.defano.wyldcard.importer.misc.ImportResult;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@SuppressWarnings("unused")
public abstract class Block implements PatternDecoder, VersionDecoder, WOBAImageDecoder, MacRomanDecoder {

    // Ignore when serializing; creates cycle in object graph
    private transient final HyperCardStack stack;

    private final BlockType blockType;
    private final int blockSize;
    private final int blockId;
    private final byte[] blockData;

    public Block(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        if (stack == null) {
            throw new IllegalArgumentException("Stack value cannot be null; each block must belong to a parent stack object.");
        }

        this.stack = stack;
        this.blockType = blockType;
        this.blockSize = blockSize;
        this.blockId = blockId;
        this.blockData = blockData;
    }

    /**
     * Unpacks (de-serializes) the block data stored in {@link #getBlockData()} into block-specific Java field.
     *
     * @param report An ImportResult object to be modified with any encountered issues that occur while unpacking.
     * @throws ImportException Thrown if a fatal error occurs unpacking the data, typically caused by malformed or unexpected values in the data.
     */
    public abstract void unpack(ImportResult report) throws ImportException;

    /**
     * Gets the HyperCard stack object to which this block belongs.
     *
     * @return The parent stack object.
     */
    public HyperCardStack getStack() {
        return stack;
    }

    /**
     * The enums of block. See {@link BlockType} for details.
     *
     * @return The block enums.
     */
    public BlockType getBlockType() {
        return blockType;
    }

    /**
     * Block size including size, enums and ID fields.
     *
     * @return The total block size, in bytes.
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * The ID of this block; a unique value used to identify this block from others of the same enums.
     *
     * @return The block id
     */
    public int getBlockId() {
        return blockId;
    }

    /**
     * The data associated with the block; all of the bytes directly following the block header. Size of the block
     * data is equal to {@link #getBlockSize()} - 16 bytes (accounting for the enums, size and id fields)
     *
     * @return The block data
     */
    public byte[] getBlockData() {
        return blockData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        ToStringBuilder.setDefaultStyle(ToStringStyle.MULTI_LINE_STYLE);
        return ToStringBuilder.reflectionToString(this);
    }
}
