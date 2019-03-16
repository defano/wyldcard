package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.misc.ImportException;
import com.defano.wyldcard.importer.misc.StackInputStream;
import com.defano.wyldcard.importer.misc.ImportResult;
import com.defano.wyldcard.importer.record.StyleRecord;

import java.io.IOException;

@SuppressWarnings("unused")
public class StyleTableBlock extends Block {

    private int styleCount;
    private int nextStyleId;
    private StyleRecord[] styles;

    public StyleTableBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    public int getStyleCount() {
        return styleCount;
    }

    public int getNextStyleId() {
        return nextStyleId;
    }

    public StyleRecord[] getStyles() {
        return styles;
    }

    @Override
    public void unpack(ImportResult report) throws ImportException {
        StackInputStream sis = new StackInputStream(getBlockData());

        try {

            styleCount = sis.readInt();
            nextStyleId = sis.readInt();
            styles = new StyleRecord[styleCount];

            for (int styleIdx = 0; styleIdx < styleCount; styleIdx++) {
                byte[] styleRecord = sis.readBytes(24);
                styles[styleIdx] = StyleRecord.deserialize(this, styleRecord, report);
            }

        } catch (IOException e) {

        }
    }
}
