package com.defano.wyldcard.importer.misc;

import com.defano.wyldcard.importer.decoder.MacRomanDecoder;

import java.io.*;

public class StackInputStream extends DataInputStream implements MacRomanDecoder {

    public StackInputStream(InputStream is) {
        super(is);
    }

    public StackInputStream(byte[] data) {
        this(new ByteArrayInputStream(data));
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

    /**
     * Reads a fixed number of MacRoman charset-encoded characters (Pascal-style).
     * @param characters The number of bytes/characters to read from the stream.
     * @return The string
     * @throws IOException Thrown if an error occurs reading from the stream.
     */
    public String readString(int characters) throws IOException {
        StringBuffer sb = new StringBuffer();

        for (int charIdx = 0; charIdx < characters; charIdx++) {
            sb.append(convertMacRomanToUnicode(readByte()));
        }

        // Convert returns to newlines
        return sb.toString().replace('\r', '\n');
    }

    /**
     * Reads a null-terminated (C-style), MacRoman charset-encoded string.
     * @return The string
     * @throws IOException Thrown if an error occurs reading from the stream.
     */
    public String readString() throws IOException {

        StringBuffer sb = new StringBuffer();

        byte thisByte;
        do {
            thisByte = readByte();
            sb.append(convertMacRomanToUnicode(thisByte));
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
}
