package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.ImportException;
import com.defano.wyldcard.importer.StackInputStream;
import com.defano.wyldcard.importer.result.ImportResult;
import com.defano.wyldcard.importer.type.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unused")
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
    private PartContent[] contents;
    private String name; // the name of the card
    private String script; // the card script

    public CardBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId) {
        super(stack, blockType, blockSize, blockId);
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

    public PartContent[] getContents() {
        return contents;
    }

    public String getName() {
        return name;
    }

    public String getScript() {
        return script;
    }

    public short getNextPartId() {
        return nextPartId;
    }

    public int getPartListSize() {
        return partListSize;
    }

    public int getPartContentSize() {
        return partContentSize;
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

                parts[partIdx] = Part.deserialize(this, entrySize, entryData, report);
            }

            // Deserialize text formatting
            contents = new PartContent[partContentCount];
            for (int partContentsIdx = 0; partContentsIdx < partContentCount; partContentsIdx++) {
                short partId = sis.readShort();
                short length = sis.readShort();
                byte[] partContentsData = sis.readBytes(length);

                contents[partContentsIdx] = PartContent.deserialize(this, partId, partContentsData, report);

                if (length % 2 != 0) {
                    sis.readByte();
                }
            }

            name = sis.readString();
            script = sis.readString();

        } catch (IOException e) {
            report.throwError(this, "Layer block is malformed; stack is corrupt.");
        }
    }

}
