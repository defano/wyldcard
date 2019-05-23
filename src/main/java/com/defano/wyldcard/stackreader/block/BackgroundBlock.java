package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.enums.LayerFlag;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.record.PartContentRecord;

import java.io.IOException;
import java.util.Arrays;

/**
 * Represents a HyperCard background.
 */
@SuppressWarnings("unused")
public class BackgroundBlock extends CardLayerBlock {

    private int bitmapId;
    private LayerFlag[] flags;
    private int cardCount;
    private int nextBkgndId;
    private int prevBkgndId;
    private short partCount;

    public BackgroundBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getPartCount() {
        return partCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartContentRecord getPartContents(int partId) {
        return Arrays.stream(getContents())
                .filter(pcr -> pcr.getRawPartId() == partId)
                .findFirst()
                .orElse(new PartContentRecord());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBitmapId() {
        return bitmapId;
    }

    /**
     * Gets an array of layer flags associated with this background (like cant-delete, hide picture, don't search).
     *
     * @return Layer flags associated with this background.
     */
    public LayerFlag[] getFlags() {
        return flags;
    }

    /**
     * Gets the number of cards sharing this background.
     *
     * @return The number of cards in this background.
     */
    public int getCardCount() {
        return cardCount;
    }

    /**
     * Gets the ID of the next background in the stack; the last background in the stack returns the ID of the first
     * background in the stack (like a circular buffer).
     *
     * @return The ID of the next background.
     */
    public int getNextBkgndId() {
        return nextBkgndId;
    }

    /**
     * Gets the ID of the previous background in the stack; the first background in the stack returns the id of the
     * last background in the stack (like a circular buffer).
     *
     * @return The ID of the previous background.
     */
    public int getPrevBkgndId() {
        return prevBkgndId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unpack() throws ImportException {

        try (StackInputStream sis = new StackInputStream(getBlockData())) {
            bitmapId = sis.readInt();
            flags = LayerFlag.fromBitmask(sis.readShort());
            sis.readShort();
            cardCount = sis.readInt();
            nextBkgndId = sis.readInt();
            prevBkgndId = sis.readInt();
            partCount = sis.readShort();

            // Unpack fields common to both cards and backgrounds
            super.unpack(sis);

        } catch (IOException e) {
            throw new ImportException(this, "Malformed BKGD (background) block.", e);
        }

    }
}
