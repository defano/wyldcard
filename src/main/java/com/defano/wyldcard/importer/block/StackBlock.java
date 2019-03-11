package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.type.StackFlag;
import com.defano.wyldcard.importer.type.StackFormat;
import com.defano.wyldcard.importer.StackInputStream;
import com.defano.wyldcard.importer.result.Results;

import java.awt.image.BufferedImage;
import java.io.IOException;

@SuppressWarnings("unused")
public class StackBlock extends Block {

    private StackFormat format;
    private StackFlag[] flags;
    private BufferedImage[] patterns = new BufferedImage[40];

    public int formatId;
    public int totalSize;
    public int stackSize;
    public int bkgndCount;
    public int firstBkgndId;
    public int cardCount;
    public int firstCardId;
    public int listId;
    public int freeCount;
    public int freeSize;
    public int printId;
    public int password;
    public short userLevel;
    public short flagsMap;
    public int createVersion;
    public int compactVersion;
    public int modifyVersion;
    public int openVersion;
    public int checksum;
    public short windowTop;
    public short windowLeft;
    public short windowBottom;
    public short windowRight;
    public short screenTop;
    public short screenLeft;
    public short screenBottom;
    public short screenRight;
    public short scrollX;
    public short scrollY;
    public int fontTableId;
    public int styleTableId;
    public short height;
    public short width;
    public long[] patternData;        // 40 patterns
    public String stackScript;

    @Override
    public StackBlock deserialize(byte[] data, Results results) {

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
            e.printStackTrace();
            return null;
        }

        return this;
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
}