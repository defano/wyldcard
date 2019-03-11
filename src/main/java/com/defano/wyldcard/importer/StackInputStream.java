package com.defano.wyldcard.importer;

import java.io.*;

public class StackInputStream extends DataInputStream {

    public StackInputStream(File file) throws FileNotFoundException {
        super(new FileInputStream(file));
    }

    public StackInputStream(byte[] data) {
        super(new ByteArrayInputStream(data));
    }

    public short[] readShort(int count) throws IOException {
        short[] shorts = new short[count];

        for (int idx = 0; idx < count; idx++) {
            shorts[idx] = readShort();
        }

        return shorts;
    }

    public int[] readInt(int count) throws IOException {
        int[] ints = new int[count];

        for (int idx = 0; idx < count; idx++) {
            ints[idx] = readInt();
        }

        return ints;
    }

    public long[] readLong(int count) throws IOException {
        long[] longs = new long[count];

        for (int idx = 0; idx < count; idx++) {
            longs[idx] = readLong();
        }

        return longs;
    }

    public byte[] readBytes(int count) throws IOException {
        byte[] buffer = new byte[count];
        readFully(buffer);
        return buffer;
    }

    public String readString() throws IOException {

        StringBuffer sb = new StringBuffer();

        byte thisByte;
        do {
            thisByte = readByte();
            sb.append(convertChar(thisByte));
        } while (thisByte != 0x00);

        // Remove null terminator byte
        sb.deleteCharAt(sb.length() - 1);

        // Convert returns to newlines
        return sb.toString().replace('\r', '\n');
    }

    public void skipToOffset(int offset) throws IOException {
        reset();
        skipBytes(offset);
    }

    private char convertChar(byte v) {
        int c = v & 0xff;
        if (c > 128) {
            return (char) macRomanToUnicode[c - 128];
        } else {
            return (char) c;
        }
    }

    private final int[] macRomanToUnicode = new int[]{
            196, 197, 199, 201, 209, 214, 220, 225, 224, 226, 228, 227, 229, 231, 233, 232,
            234, 235, 237, 236, 238, 239, 241, 243, 242, 244, 246, 245, 250, 249, 251, 252,
            8224, 176, 162, 163, 167, 8226, 182, 223, 174, 169, 8482, 180, 168, 8800, 198, 216,
            8734, 177, 8804, 8805, 165, 181, 8706, 8721, 8719, 960, 8747, 170, 186, 937, 230, 248,
            191, 161, 172, 8730, 402, 8776, 8710, 171, 187, 8230, 160, 192, 195, 213, 338, 339,
            8211, 8212, 8220, 8221, 8216, 8217, 247, 9674, 255, 376, 8260, 8364, 8249, 8250, 64257, 64258,
            8225, 183, 8218, 8222, 8240, 194, 202, 193, 203, 200, 205, 206, 207, 204, 211, 212,
            63743, 210, 218, 219, 217, 305, 710, 732, 175, 728, 729, 730, 184, 733, 731, 711};

}
