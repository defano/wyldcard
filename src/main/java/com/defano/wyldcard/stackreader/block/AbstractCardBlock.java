package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.misc.ImportResult;
import com.defano.wyldcard.stackreader.record.PartRecord;
import com.defano.wyldcard.stackreader.record.PartContentRecord;

import java.io.IOException;

/**
 * Represents the fields common to CARD and BKGD blocks.
 */
@SuppressWarnings("unused")
public abstract class AbstractCardBlock extends Block {

    private short nextPartId;
    private int partListSize;
    private short partContentCount; // number of part contents
    private int partContentSize;
    private PartRecord[] parts;
    private PartContentRecord[] contents;
    private String name; // the name of the card
    private String script; // the card script

    @SuppressWarnings("WeakerAccess")
    public AbstractCardBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    public abstract short getPartCount();

    public short getPartContentCount() {
        return partContentCount;
    }

    public PartRecord[] getParts() {
        return parts;
    }

    public PartContentRecord[] getContents() {
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

    public void unpack(StackInputStream sis, ImportResult report) throws ImportException {

        int partCount = getPartCount();

        try {
            nextPartId = sis.readShort();
            partListSize = sis.readInt();
            partContentCount = sis.readShort();
            partContentSize = sis.readInt();

            // Deserialize buttons and fields
            parts = new PartRecord[partCount];
            for (int partIdx = 0; partIdx < partCount; partIdx++) {
                short entrySize = sis.readShort();
                byte[] entryData = sis.readBytes(entrySize - 2);

                parts[partIdx] = PartRecord.deserialize(this, entrySize, entryData, report);
            }

            // Deserialize text formatting
            contents = new PartContentRecord[partContentCount];
            for (int partContentsIdx = 0; partContentsIdx < partContentCount; partContentsIdx++) {
                short partId = sis.readShort();
                short length = sis.readShort();
                byte[] partContentsData = sis.readBytes(length);

                contents[partContentsIdx] = PartContentRecord.deserialize(this, partId, partContentsData, report);

                if (length % 2 != 0) {
                    sis.readByte();
                }
            }

            name = sis.readString();
            script = sis.readString();

        } catch (IOException e) {
            report.throwError(this, "Malformed CARD or BGND block.");
        }
    }
}
