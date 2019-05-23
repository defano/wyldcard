package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;

import java.io.IOException;

@SuppressWarnings("unused")
public class TailBlock extends Block {

    private int tailStringLength;
    private String tailString;

    public TailBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    public int getTailStringLength() {
        return tailStringLength;
    }

    public String getTailString() {
        return tailString;
    }

    @Override
    public void unpack() throws ImportException {

        try (StackInputStream sis = new StackInputStream(getBlockData())) {
            tailStringLength = sis.readUnsignedByte();
            tailString = sis.readString(tailStringLength);
        } catch (IOException e) {
            throw new ImportException(this, "Malformed TAIL block.", e);
        }
    }
}
