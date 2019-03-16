package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.misc.ImportException;
import com.defano.wyldcard.importer.misc.StackInputStream;
import com.defano.wyldcard.importer.misc.ImportResult;

import java.io.IOException;

@SuppressWarnings("unused")
public class FreeBlock extends Block {

    private byte markerLength;
    private String markerText;
    private byte[] freeData;

    public FreeBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    public byte getMarkerLength() {
        return markerLength;
    }

    public String getMarkerText() {
        return markerText;
    }

    public byte[] getFreeData() {
        return freeData;
    }

    @Override
    public void unpack(ImportResult report) throws ImportException {
        StackInputStream sis = new StackInputStream(getBlockData());

        try {

            markerLength = sis.readByte();
            markerText = sis.readString(markerLength);

            freeData = sis.readBytes(getBlockSize() - 18 - markerLength);

        } catch (IOException e) {
            report.throwError(this, "Malformed FREE block.");
        }
    }
}
