package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.record.FontRecord;

import java.io.IOException;
import java.util.Arrays;

/**
 * Provides a list of {@link FontRecord} objects describing all the fonts used in the stack.
 */
@SuppressWarnings("unused")
public class FontTableBlock extends Block {

    private int fontCount;
    private FontRecord[] fonts = new FontRecord[0];

    public FontTableBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    /**
     * Gets the number of FontRecords in this block.
     *
     * @return The number of FontRecords.
     */
    public int getFontCount() {
        return fontCount;
    }

    /**
     * Gets all FontRecords held in this block.
     *
     * @return All FontRecords.
     */
    public FontRecord[] getFonts() {
        return fonts;
    }

    /**
     * Gets the {@link FontRecord} associated with the given ID.
     *
     * @param fontId The ID of the font record to retrieve.
     * @return The associated FontRecord.
     */
    public FontRecord getFont(int fontId) {
        return Arrays.stream(getFonts())
                .filter(f -> f.getFontId() == fontId).findFirst()
                .orElse(fonts[0]);      // TODO: Some stacks reference fonts not in the font table block... wtf?
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unpack() throws IOException {

        try (StackInputStream sis = new StackInputStream(getBlockData())) {
            fontCount = sis.readInt();
            sis.readInt();

            fonts = new FontRecord[fontCount];
            for (int fontIdx = 0; fontIdx < fontCount; fontIdx++) {
                short fontId = sis.readShort();
                String fontName = sis.readString();

                fonts[fontIdx] = new FontRecord(fontId, fontName);

                // Word align records; the +1 accounts for null terminator stripped when reading string
                if ((fontName.length() + 1) % 2 != 0) {
                    sis.readByte();
                }
            }
        }
    }
}
