package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.misc.ImportException;
import com.defano.wyldcard.importer.misc.StackInputStream;
import com.defano.wyldcard.importer.misc.ImportResult;
import com.defano.wyldcard.importer.enums.CardFlag;

import java.io.IOException;

@SuppressWarnings("unused")
public class BackgroundBlock extends AbstractCardBlock {

    private int bitmapId;
    private CardFlag[] flags;
    private int cardCount;
    private int nextBkgndId;
    private int prevBkgndId;
    private short partCount;

    public BackgroundBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    @Override
    public short getPartCount() {
        return partCount;
    }

    public int getBitmapId() {
        return bitmapId;
    }

    public CardFlag[] getFlags() {
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
            flags = CardFlag.fromBitmask(sis.readShort());
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
