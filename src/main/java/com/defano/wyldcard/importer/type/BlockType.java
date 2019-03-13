package com.defano.wyldcard.importer.type;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.block.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
    PAGE(PageBlock.class),

    /**
     * Background
     */
    BKGD,

    /**
     * Card
     */
    CARD(CardBlock.class),

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

        return null;
    }

    public Block instantiateBlock(HyperCardStack stack, int blockId, int blockSize) {
        if (klass != null) {
            try {
                Constructor constructor = klass.getConstructor(HyperCardStack.class, BlockType.class, int.class, int.class);
                return  (Block) constructor.newInstance(stack, this, blockSize, blockId);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                // Somebody changed the Block constructor signature... tsk, tsk
                throw new IllegalStateException("Bug! Can't instantiate " + klass);
            }
        }

        return null;
    }
}
