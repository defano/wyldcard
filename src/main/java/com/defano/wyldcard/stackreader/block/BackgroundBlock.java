package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.misc.ImportResult;
import com.defano.wyldcard.stackreader.enums.LayerFlag;
import com.defano.wyldcard.stackreader.record.PartContentRecord;

import java.io.IOException;
import java.util.Arrays;

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

    /** {@inheritDoc} */
    @Override
    public short getPartCount() {
        return partCount;
    }

    /** {@inheritDoc} */
    @Override
    public PartContentRecord getPartContents(int partId) {
        return Arrays.stream(getContents())
                .filter(pcr -> pcr.getRawPartId() == partId)
                .findFirst()
                .orElse(new PartContentRecord());
    }

    /** {@inheritDoc} */
    @Override
    public int getBitmapId() {
        return bitmapId;
    }

    public LayerFlag[] getFlags() {
        return flags;
    }

    public int getCardCount() {
        return cardCount;
    }

    public int getNextBkgndId() {
        return nextBkgndId;
    }

    public int getPrevBkgndId() {
        return prevBkgndId;
    }

    @Override
    public void unpack(ImportResult report) throws ImportException {
        StackInputStream sis = new StackInputStream(getBlockData());

        try {
            bitmapId = sis.readInt();
            flags = LayerFlag.fromBitmask(sis.readShort());
            sis.readShort();
            cardCount = sis.readInt();
            nextBkgndId = sis.readInt();
            prevBkgndId = sis.readInt();
            partCount = sis.readShort();

            // Unpack fields common to both cards and backgrounds
            super.unpack(sis, report);

        } catch (IOException e) {
            report.throwError(this, "Malformed BKGD block.");
        }

    }
}
