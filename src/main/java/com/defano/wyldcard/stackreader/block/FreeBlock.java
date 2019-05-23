package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;

import java.io.IOException;

@SuppressWarnings("unused")
public class FreeBlock extends Block {

    private byte markerLength;
    private String markerText;

    public FreeBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    public byte getMarkerLength() {
        return markerLength;
    }

    public String getMarkerText() {
        return markerText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unpack() throws ImportException {

        try (StackInputStream sis = new StackInputStream(getBlockData())) {

            markerLength = sis.readByte();
            markerText = sis.readString(markerLength);

        } catch (IOException e) {
            throw new ImportException(this, "Malformed FREE block.", e);
        }
    }
}
