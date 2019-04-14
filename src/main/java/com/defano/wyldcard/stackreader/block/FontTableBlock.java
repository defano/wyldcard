package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.misc.ImportResult;
import com.defano.wyldcard.stackreader.record.FontRecord;

import java.io.IOException;
import java.util.Arrays;

@SuppressWarnings("unused")
public class FontTableBlock extends Block {

    private int fontCount;
    private FontRecord[] fonts = new FontRecord[0];

    public FontTableBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    public int getFontCount() {
        return fontCount;
    }

    public FontRecord[] getFonts() {
        return fonts;
    }

    public FontRecord getFont(int fontId) {
        return Arrays.stream(getFonts())
                .filter(f -> f.getFontId() == fontId).findFirst()
                .orElse(fonts[0]);      // TODO: Some stacks reference unknown fonts...?
    }

    @Override
    public void unpack(ImportResult report) throws ImportException {
        StackInputStream sis = new StackInputStream(getBlockData());

        try {
            fontCount = sis.readInt();
            sis.readInt();

            fonts = new FontRecord[fontCount];
            for (int fontIdx = 0; fontIdx < fontCount; fontIdx++) {
                short fontId = sis.readShort();
                String fontName = sis.readString();
                System.err.println("GOT " + fontName + " id: " + fontId);

                fonts[fontIdx] = new FontRecord(fontId, fontName);

                // Word align records; the +1 accounts for null terminator stripped when reading string
                if ((fontName.length() + 1) % 2 != 0) {
                    sis.readByte();
                }
            }

        } catch (IOException e) {
            report.throwError(this, "Malformed FTBL (font table) block; stack is corrupted.");
        }
    }
}
