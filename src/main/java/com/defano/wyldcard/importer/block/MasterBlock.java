package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.misc.ImportException;
import com.defano.wyldcard.importer.misc.ImportResult;
import com.defano.wyldcard.importer.misc.StackInputStream;
import com.defano.wyldcard.importer.record.BlockOffsetRecord;

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
    public void unpack(ImportResult report) throws ImportException {
        StackInputStream sis = new StackInputStream(getBlockData());
        ArrayList<BlockOffsetRecord> records = new ArrayList<>();

        try {
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
            report.throwError(this, "Malformed MAST block.");
        }
    }
}