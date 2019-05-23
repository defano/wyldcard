package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.misc.ImportException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * An enumeration of block types that comprise a HyperCard stack.
 */
public enum BlockType {

    /**
     * The stack header. Always present in the stack, always the first block in the file, and never appears more than
     * once.
     */
    STAK(StackBlock.class),

    /**
     * The master reference. Always present, always the second block in the file. Contains an index of all the blocks
     * present in a stack file, excluding STAK, MAST, FREE and TAIL blocks.
     */
    MAST(MasterBlock.class),

    /**
     * The card page index list. Appears zero or one time, anywhere in the file. Not present in stacks where there are
     * no {@link #PAGE} blocks.
     */
    LIST(ListBlock.class),

    /**
     * A card index list. Appears zero or more times; we assume in cases where this block is absent, card order
     * is defined by {@link #CARD} block order in the file.
     */
    PAGE(PageBlock.class),

    /**
     * A background. Appears one or more times, and describes a background in the stack (including all buttons and
     * fields that appear on it).
     */
    BKGD(BackgroundBlock.class),

    /**
     * A card. Appears one or more times, and describes a card in the stack (including all buttons and fields on the
     * card).
     */
    CARD(CardBlock.class),

    /**
     * A card or background bitmap image (specified in "Wrath of Bill Atkinson" format, see
     * {@link com.defano.wyldcard.stackreader.decoder.WOBAImageDecoder}).
     */
    BMAP(ImageBlock.class),

    /**
     * A free space (deleted block). Appears zero or more times. These blocks would be removed in HyperCard using the
     * "Compact Stack" command.
     */
    FREE(FreeBlock.class),

    /**
     * A style table. Appears once in a stack and describes the set of text styles used in the stack.
     */
    STBL(StyleTableBlock.class),

    /**
     * The font table. Appears once in a stack and describes the fonts (by name) used by the stack.
     */
    FTBL(FontTableBlock.class),

    /**
     * Print settings and template index. Appears once in a stack and describes "Print Report" settings.
     */
    PRNT(PrintTableBlock.class),

    /**
     * Page setup settings. Appears once in a stack and describes the "Page Setup" settings.
     */
    PRST(PageSetupBlock.class),

    /**
     * Print report template. Appears zero or more times in a stack and describes the report templates (for printing)
     * refenced by the {@link #PRNT} block.
     */
    PRFT(PrintReportBlock.class),

    /**
     * Tail object ("Nu är det slut…" or "That's all folks"). Appears once (always last) in the stack.
     */
    TAIL(TailBlock.class);

    private final Class<? extends Block> klass;

    <T extends Block> BlockType(Class<T> klass) {
        this.klass = klass;
    }

    /**
     * Gets the BlockType associated with the given numeric block ID.
     *
     * @param blockId The numeric block ID.
     * @return The associated BlockType.
     * @throws ImportException Thrown if no such BlockType is associated with the given blockId.
     */
    public static BlockType fromBlockId(int blockId) throws ImportException {
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

        throw new ImportException("Not a valid block identifier: " + blockId);
    }

    /**
     * Attempts to instantiate a Block of the given type, give its serialized data.
     *
     * @param stack     The stack to which the block belongs.
     * @param blockId   The ID of the block.
     * @param blockSize The size (in bytes) of the block.
     * @param blockData The block data array, containing all the bytes following the block type, id, size, and 4-byte
     *                  padding. Therefore, the size of this array must equal the blockSize - 16 (the 16 bytes account
     *                  for the type, id, size and padding not present in the array).
     * @return The instantiated block object, unpacked (that is, the block data has been parsed into individual fields
     * and is available from the block's getter methods.
     * @throws ImportException Thrown if an error occurs while unpacking the blockData.
     */
    public Block instantiate(HyperCardStack stack, int blockId, int blockSize, byte[] blockData) throws ImportException {
        try {
            Constructor constructor = klass.getConstructor(HyperCardStack.class, BlockType.class, int.class, int.class, byte[].class);
            Block b = (Block) constructor.newInstance(stack, this, blockSize, blockId, blockData);
            b.unpack();
            return b;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            // Somebody changed the Block constructor signature... tsk, tsk
            throw new IllegalStateException("Bug! Can't instantiate " + klass);
        }
    }

    /**
     * Gets the Block class type associated with this BlockType.
     *
     * @return The associated Block class.
     */
    public Class<? extends Block> blockClass() {
        return klass;
    }
}
