package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum BlockType {

    /**
     * Stack header. Always the first block in the file and never appears more than once.
     */
    STAK(StackBlock.class),

    /**
     * Master reference object; an index of all the blocks present in a stack file, excluding STAK, MAST, FREE and TAIL
     * blocks.
     */
    MAST(MasterBlock.class),

    /**
     * Card index list.
     */
    LIST(ListBlock.class),

    /**
     * Card index
     */
    PAGE(PageBlock.class, 1),

    /**
     * Background; describes a background in the stack including all buttons and fields on it.
     */
    BKGD(BackgroundBlock.class),

    /**
     * Card; describes a card in the stack including all buttons and fields on the card.
     */
    CARD(CardBlock.class),

    /**
     * Card or background bitmap image ("WOBA" format)
     */
    BMAP(ImageBlock.class),

    /**
     * Free space (removed using the "Compact Stack" command)
     */
    FREE(FreeBlock.class),

    /**
     * Style table
     */
    STBL(StyleTableBlock.class),

    /**
     * Font table
     */
    FTBL(FontTableBlock.class),

    /**
     * Print settings and template index
     */
    PRNT(PrintTableBlock.class),

    /**
     * Page setup settings
     */
    PRST(PageSetupBlock.class),

    /**
     * Print report template
     */
    PRFT(PrintReportBlock.class),

    /**
     * Tail object ("Nu är det slut…" or "That's all folks")
     */
    TAIL(TailBlock.class);

    private final Class<? extends Block> klass;
    private final int importOrder;

    <T extends Block> BlockType(Class<T> klass) {
        this(klass, 0);
    }

    <T extends Block> BlockType(Class<T> klass, int importOrder) {
        this.klass = klass;
        this.importOrder = importOrder;
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

    public Block instantiateBlock(HyperCardStack stack, int blockId, int blockSize, byte[] blockData) {
        if (klass != null) {
            try {
                Constructor constructor = klass.getConstructor(HyperCardStack.class, BlockType.class, int.class, int.class, byte[].class);
                return  (Block) constructor.newInstance(stack, this, blockSize, blockId, blockData);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                // Somebody changed the Block constructor signature... tsk, tsk
                throw new IllegalStateException("Bug! Can't instantiate " + klass);
            }
        }

        return null;
    }

    public Class<? extends Block> blockClass() {
        return klass;
    }

    public int getImportOrder() {
        return importOrder;
    }
}
