package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.record.BlockOffsetRecord;

import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class MasterBlock extends Block {

    private BlockOffsetRecord[] blockOffsets = new BlockOffsetRecord[0];

    public MasterBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    public BlockOffsetRecord[] getBlockOffsets() {
        return blockOffsets;
    }

    @Override
    public void unpack() throws ImportException {
        ArrayList<BlockOffsetRecord> records = new ArrayList<>();

        try (StackInputStream sis = new StackInputStream(getBlockData())) {
            sis.readBytes(16);

            for (int idx = 0; idx < (getBlockSize() - 32) / 4; idx++) {
                int record = sis.readInt();

                if (record != 0) {
                    int offset = (record & 0xffffff00) >> 8;
                    byte blockId = (byte) (record & 0xff);

                    records.add(new BlockOffsetRecord(offset, blockId));
                }
            }

            blockOffsets = records.toArray(new BlockOffsetRecord[0]);

        } catch (IOException e) {
            throw new ImportException(this, "Malformed MAST block.", e);
        }
    }
}
