package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.enums.LayerFlag;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.record.PartContentRecord;

import java.io.IOException;
import java.util.Arrays;

/**
 * Represents a card in a HyperCard stack.
 */
@SuppressWarnings("unused")
public class CardBlock extends CardLayerBlock {

    private int bitmapId;
    private LayerFlag[] flags;
    private int pageId; // ID number of the PAGE block containing this card's index
    private int bkgndId; // ID number of the card's background
    private short partCount; // number of parts (buttons and fields) on this card

    public CardBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    /** {@inheritDoc} */
    @Override
    public int getBitmapId() {
        return bitmapId;
    }

    /**
     * Gets an array of card flags. See {@link LayerFlag} for details.
     *
     * @return Zero or more card flags.
     */
    public LayerFlag[] getFlags() {
        return flags;
    }

    /**
     * Gets the ID of the PAGE block that indexes this card. See {@link PageBlock} for details.
     *
     * @return The ID of the page block.
     */
    public int getPageId() {
        return pageId;
    }

    /**
     * Gets the ID of the BGND block that describes the background layer associated with this card. Use
     * {@link #getBkgndBlock()} to retrieve the block data structure directly.
     * <p>
     * This is the same value as the background's ID as shown in HyperCard. See {@link BackgroundBlock} for details.
     *
     * @return The ID of this card's background.
     */
    public int getBkgndId() {
        return bkgndId;
    }

    /**
     * Gets the {@link BackgroundBlock} structure representing this card's background. See {@link BackgroundBlock} for
     * details.
     *
     * @return This card's background block structure
     */
    public BackgroundBlock getBkgndBlock() {
        return getStack().getBlock(BackgroundBlock.class, getBkgndId());
    }

    /** {@inheritDoc} */
    @Override
    public PartContentRecord getPartContents(int partId) {
        return Arrays.stream(getContents())
                .filter(pcr -> pcr.getRawPartId() == -partId)
                .findFirst()
                .orElse(new PartContentRecord());
    }

    /** {@inheritDoc} */
    @Override
    public short getPartCount() {
        return partCount;
    }

    /** {@inheritDoc} */
    @Override
    public void unpack() throws ImportException {

        try (StackInputStream sis = new StackInputStream(getBlockData())) {
            bitmapId = sis.readInt();
            flags = LayerFlag.fromBitmask(sis.readShort());
            sis.skipBytes(10);
            pageId = sis.readInt();
            bkgndId = sis.readInt();
            partCount = sis.readShort();

            // Unpack fields common to both cards and backgrounds
            super.unpack(sis);

        } catch (IOException e) {
            throw new ImportException(this, "Layer block is malformed; stack is corrupt.", e);
        }
    }

}
