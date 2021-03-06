package com.defano.wyldcard.stackreader.decoder;

import java.awt.image.BufferedImage;

/**
 * A mixin interface used to decode a HyperCard pattern to a {@link BufferedImage}.
 */
public interface PatternDecoder {

    /**
     * Decodes a HyperCard pattern bitmap into an 8x8 pixel {@link BufferedImage}.
     *
     * @param pattern The pattern bitmap.
     * @return An 8x8 BufferedImage containing the pattern.
     */
    @SuppressWarnings("PointlessArithmeticExpression")
    default BufferedImage decodePattern(long pattern) {
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

}
