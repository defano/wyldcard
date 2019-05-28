package com.defano.wyldcard.stackreader.decoder;

public interface ChecksumDecoder {

    /**
     * Rotates the unsigned value of n, 3 bits to the right (bits shifted off the left of the value are appended on the
     * right).
     *
     * @param n The 32-bit, unsigned value to rotate
     * @return The rotated value.
     */
    default int rotate(int n) {
        final int INT_LENGTH = 32;
        final int ROTATE_BITS = 3;

        long val = n & 0xffffffffL;
        return (int) ((val >> ROTATE_BITS) | (val << (INT_LENGTH - ROTATE_BITS)) & 0xffffffffL);
    }
}
