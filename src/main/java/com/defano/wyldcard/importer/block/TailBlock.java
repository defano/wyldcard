package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.misc.ImportException;
import com.defano.wyldcard.importer.misc.StackInputStream;
import com.defano.wyldcard.importer.misc.ImportResult;

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
    public void unpack(ImportResult report) throws ImportException {
        StackInputStream sis = new StackInputStream(getBlockData());

        try {
            tailStringLength = sis.readUnsignedByte();
            tailString = sis.readString(tailStringLength);
        } catch (IOException e) {
            report.throwError(this, "Malformed TAIL block.");
        }
    }
}
