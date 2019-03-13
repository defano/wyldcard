package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.ImportException;
import com.defano.wyldcard.importer.StackInputStream;
import com.defano.wyldcard.importer.result.ImportResult;
import com.defano.wyldcard.importer.type.BlockType;
import com.defano.wyldcard.importer.type.CardFlag;
import com.defano.wyldcard.importer.type.Part;
import com.defano.wyldcard.importer.type.PartContent;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class CardBlock extends Block {

    private int bitmapId; // ID number of the corresponding BMAP block
    private List<CardFlag> flags;
    private int pageId; // ID number of the PAGE block containing this card's index
    private int bkgndId; // ID number of the card's background
    private short partCount; // number of parts (buttons and fields) on this card
    private short nextPartId;
    private int partListSize;
    private short partContentCount; // number of part contents
    private int partContentSize;
    private Part[] parts;
    private PartContent[] partContents;
    private String contents;
    private String cardName; // the name of the card
    private String cardScript; // the card script

    public CardBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId) {
        super(stack, blockType, blockSize, blockId);
    }

    @Override
    public void deserialize(byte[] data, ImportResult report) throws ImportException {
        StackInputStream sis = new StackInputStream(data);

        try {
            bitmapId = sis.readInt();
            flags = CardFlag.fromBitmask(sis.readShort());
            sis.skipBytes(10);
            pageId = sis.readInt();
            bkgndId = sis.readInt();
            partCount = sis.readShort();
            nextPartId = sis.readShort();
            partListSize = sis.readInt();
            partContentCount = sis.readShort();
            partContentSize = sis.readInt();

            // Deserialize buttons and fields
            parts = new Part[partCount];
            for (int partIdx = 0; partIdx < partCount; partIdx++) {
                short entrySize = sis.readShort();
                byte[] entryData = sis.readBytes(entrySize - 2);

                parts[partIdx] = new Part(entrySize, entryData);
            }

            // Deserialize text formatting
            partContents = new PartContent[partContentCount];
            for (int partContentsIdx = 0; partContentsIdx < partContentCount; partContentsIdx++) {
                short partId = sis.readShort();
                short length = sis.readShort();
                byte[] contentsData = sis.readBytes(length);

                partContents[partContentsIdx] = new PartContent(partId, contentsData);
            }

        } catch (IOException e) {
            report.throwError(this, "Layer block is malformed; stack is corrupt.");
        }
    }

    public BufferedImage getImage() {
        return getStack().getImage(getBitmapId());
    }

    public int getBitmapId() {
        return bitmapId;
    }

    public List<CardFlag> getFlags() {
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

    public short getPartContentCount() {
        return partContentCount;
    }

    public Part[] getParts() {
        return parts;
    }

    public PartContent[] getPartContents() {
        return partContents;
    }

    public String getContents() {
        return contents;
    }

    public String getCardName() {
        return cardName;
    }

    public String getCardScript() {
        return cardScript;
    }
}
