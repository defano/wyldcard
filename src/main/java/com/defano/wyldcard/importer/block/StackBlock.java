package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.ImportException;
import com.defano.wyldcard.importer.type.BlockType;
import com.defano.wyldcard.importer.type.StackFlag;
import com.defano.wyldcard.importer.type.StackFormat;
import com.defano.wyldcard.importer.StackInputStream;
import com.defano.wyldcard.importer.result.ImportResult;

import java.awt.image.BufferedImage;
import java.io.IOException;

@SuppressWarnings("unused")
public class StackBlock extends Block {

    private StackFormat format;
    private StackFlag[] flags;
    private BufferedImage[] patterns = new BufferedImage[40];

    private int formatId;
    private int totalSize;
    private int stackSize;
    private int bkgndCount;
    private int firstBkgndId;
    private int cardCount;
    private int firstCardId;
    private int listId;
    private int freeCount;
    private int freeSize;
    private int printId;
    private int password;
    private short userLevel;
    private short flagsMap;
    private int createVersion;
    private int compactVersion;
    private int modifyVersion;
    private int openVersion;
    private int checksum;
    private short windowTop;
    private short windowLeft;
    private short windowBottom;
    private short windowRight;
    private short screenTop;
    private short screenLeft;
    private short screenBottom;
    private short screenRight;
    private short scrollX;
    private short scrollY;
    private int fontTableId;
    private int styleTableId;
    private short height;
    private short width;
    private long[] patternData;        // 40 patterns
    private String stackScript;

    public StackBlock(HyperCardStack root, BlockType blockType, int blockSize, int blockId) {
        super(root, blockType, blockSize, blockId);
    }

    @Override
    public void deserialize(byte[] data, ImportResult report) throws ImportException {

        StackInputStream sis = new StackInputStream(data);

        try {
            formatId = sis.readInt();
            format = StackFormat.fromFormatInt(formatId);
            totalSize = sis.readInt();
            stackSize = sis.readInt();

            // Unknown region; skip
            sis.readInt(2);

            bkgndCount = sis.readInt();
            firstBkgndId = sis.readInt();
            cardCount = sis.readInt();
            firstCardId = sis.readInt();
            listId = sis.readInt();
            freeCount = sis.readInt();
            freeSize = sis.readInt();
            printId = sis.readInt();
            password = sis.readInt();
            userLevel = sis.readShort();
            sis.readShort(); // Unknown region; skip
            flagsMap = sis.readShort();
            flags = StackFlag.fromBitmask(flagsMap);
            sis.readShort(); // Unknown region; skip
            sis.readInt(4); // Unknown region; skip
            createVersion = sis.readInt();
            compactVersion = sis.readInt();
            modifyVersion = sis.readInt();
            openVersion = sis.readInt();
            checksum = sis.readInt();
            sis.readInt(); // Unknown region; skip
            windowTop = sis.readShort();
            windowLeft = sis.readShort();
            windowBottom = sis.readShort();
            windowRight = sis.readShort();
            screenTop = sis.readShort();
            screenLeft = sis.readShort();
            screenBottom = sis.readShort();
            screenRight = sis.readShort();
            scrollX = sis.readShort();
            scrollY = sis.readShort();

            sis.skipToOffset(0x1b0 - 16);
            fontTableId = sis.readInt();
            styleTableId = sis.readInt();
            height = sis.readShort();
            width = sis.readShort();

            sis.skipToOffset(0x2c0 - 16);
            patternData = sis.readLong(40);

            sis.skipToOffset(0x600 - 16);
            stackScript = sis.readString();

            decodePatterns();

        } catch (IOException e) {
            report.error(this, "Malformed stack block; stack is corrupt.", e);
        }
    }

    private void decodePatterns() {
        for (int patternIdx = 0; patternIdx < 40; patternIdx++) {
            patterns[patternIdx] = decodePattern(patternIdx);
        }
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private BufferedImage decodePattern(int patternIdx) {
        long pattern = patternData[patternIdx];
        int[] pixels = new int[64];

        for (int row = 0; row < 8; row++) {
            byte rowBits = (byte) ((pattern & (0xffL << (row * 8))) >> (row * 8));

            pixels[row * 8 + 7] = ((rowBits & 0x80) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 6] = ((rowBits & 0x40) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 5] = ((rowBits & 0x20) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 4] = ((rowBits & 0x10) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 3] = ((rowBits & 0x08) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 2] = ((rowBits & 0x04) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 1] = ((rowBits & 0x02) > 0) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[row * 8 + 0] = ((rowBits & 0x01) > 0) ? 0xFF000000 : 0xFFFFFFFF;
        }

        BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 8, 8, pixels, 0, 8);

        return image;
    }

    public StackFormat getFormat() {
        return format;
    }

    public StackFlag[] getFlags() {
        return flags;
    }

    public BufferedImage[] getPatterns() {
        return patterns;
    }

    public int getFormatId() {
        return formatId;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public int getStackSize() {
        return stackSize;
    }

    public int getBkgndCount() {
        return bkgndCount;
    }

    public int getFirstBkgndId() {
        return firstBkgndId;
    }

    public int getCardCount() {
        return cardCount;
    }

    public int getFirstCardId() {
        return firstCardId;
    }

    public int getListId() {
        return listId;
    }

    public int getFreeCount() {
        return freeCount;
    }

    public int getFreeSize() {
        return freeSize;
    }

    public int getPrintId() {
        return printId;
    }

    public int getPassword() {
        return password;
    }

    public short getUserLevel() {
        return userLevel;
    }

    public short getFlagsMap() {
        return flagsMap;
    }

    public int getCreateVersion() {
        return createVersion;
    }

    public int getCompactVersion() {
        return compactVersion;
    }

    public int getModifyVersion() {
        return modifyVersion;
    }

    public int getOpenVersion() {
        return openVersion;
    }

    public int getChecksum() {
        return checksum;
    }

    public short getWindowTop() {
        return windowTop;
    }

    public short getWindowLeft() {
        return windowLeft;
    }

    public short getWindowBottom() {
        return windowBottom;
    }

    public short getWindowRight() {
        return windowRight;
    }

    public short getScreenTop() {
        return screenTop;
    }

    public short getScreenLeft() {
        return screenLeft;
    }

    public short getScreenBottom() {
        return screenBottom;
    }

    public short getScreenRight() {
        return screenRight;
    }

    public short getScrollX() {
        return scrollX;
    }

    public short getScrollY() {
        return scrollY;
    }

    public int getFontTableId() {
        return fontTableId;
    }

    public int getStyleTableId() {
        return styleTableId;
    }

    public short getHeight() {
        return height;
    }

    public short getWidth() {
        return width;
    }

    public long[] getPatternData() {
        return patternData;
    }

    public String getStackScript() {
        return stackScript;
    }
}