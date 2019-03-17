package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.misc.ImportResult;
import com.defano.wyldcard.stackreader.enums.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 */
@SuppressWarnings("unused")
public class CardBlock extends AbstractCardBlock {

    private int bitmapId; // ID number of the corresponding BMAP block
    private CardFlag[] flags;
    private int pageId; // ID number of the PAGE block containing this card's index
    private int bkgndId; // ID number of the card's background
    private short partCount; // number of parts (buttons and fields) on this card

    public CardBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    public BufferedImage getImage() {
        return getStack().getImage(getBitmapId());
    }

    public int getBitmapId() {
        return bitmapId;
    }

    public CardFlag[] getFlags() {
        return flags;
    }

    public int getPageId() {
        return pageId;
    }

    public int getBkgndId() {
        return bkgndId;
    }

    public short getPartCount() {
        return partCount;
    }

    @Override
    public void unpack(ImportResult report) throws ImportException {
        StackInputStream sis = new StackInputStream(getBlockData());

        try {
            bitmapId = sis.readInt();
            flags = CardFlag.fromBitmask(sis.readShort());
            sis.skipBytes(10);
            pageId = sis.readInt();
            bkgndId = sis.readInt();
            partCount = sis.readShort();

            // Unpack fields common to both cards and backgrounds
            super.unpack(sis, report);

        } catch (IOException e) {
            report.throwError(this, "Layer block is malformed; stack is corrupt.");
        }
    }

}
