package com.defano.wyldcard.importer.type;

import com.defano.wyldcard.importer.block.ImageBlock;
import com.defano.wyldcard.importer.block.Block;
import com.defano.wyldcard.importer.block.ListBlock;
import com.defano.wyldcard.importer.block.StackBlock;

public enum BlockType {
    /**
     * Stack header
     */
    STAK(StackBlock.class),

    /**
     * Master reference object
     */
    MAST,

    /**
     * Card index list
     */
    LIST(ListBlock.class),

    /**
     * Card index
     */
    PAGE,

    /**
     * Background
     */
    BKGD,

    /**
     * Card
     */
    CARD,

    /**
     * Card or background image ("WOBA" format)
     */
    BMAP(ImageBlock.class),

    /**
     * Free space (removed using the "Compact Stack" command)
     */
    FREE,

    /**
     * Style table
     */
    STBL,

    /**
     * Font table
     */
    FTBL,

    /**
     * Print settings and template index
     */
    PRNT,

    /**
     * Page setup settings
     */
    PRST,

    /**
     * Print report template
     */
    PRFT,

    /**
     * Tail object ("Nu är det slut…" or "That's all folks")
     */
    TAIL;

    private final Class<? extends Block> klass;

    <T extends Block> BlockType(Class<T> klass) {
        this.klass = klass;
    }

    BlockType() {
        this(null);
    }

    public static BlockType fromBlockId(int blockId) {
        for (BlockType thisType : values()) {
            String thisTypeName = thisType.name();
            char[] thisChars = thisTypeName.toCharArray();

            if (thisChars[0] == (char) ((blockId & 0xff000000) >> 24) &&
                    thisChars[1] == (char) ((blockId & 0x00ff0000) >> 16) &&
                    thisChars[2] == (char) ((blockId & 0x0000ff00) >> 8) &&
                    thisChars[3] == (char) (blockId & 0x000000ff)) {
                return thisType;
            }
        }

        throw new IllegalArgumentException("Not a valid block type");
    }

    public Block getBlockInstance(int blockId, int blockSize) {
        if (klass != null) {
            try {
                Block instance = klass.newInstance();
                instance.blockId = blockId;
                instance.blockSize = blockSize;
                instance.blockType = this;

                return instance;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Bug! Can't instantiate " + klass);
            }
        }

        return null;
    }
}
