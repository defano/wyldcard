package com.defano.wyldcard.stackreader.misc;

import com.defano.wyldcard.stackreader.decoder.MacRomanDecoder;

import java.io.*;

/**
 * An input stream with helper methods intended to simplify parsing a HyperCard stack file.
 */
public class StackInputStream extends DataInputStream implements MacRomanDecoder {

    /**
     * Create a StackInputStream from an InputStream.
     *
     * @param is The InputStream to wrap in a StackInputStream.
     */
    public StackInputStream(InputStream is) {
        super(is);
    }

    /**
     * Create a StackInputStream from an array of bytes.
     *
     * @param data The bytes that the StackInputStream will stream.
     */
    public StackInputStream(byte[] data) {
        this(new ByteArrayInputStream(data));
    }

    /**
     * Reads zero or more shorts (16-bit values) from the input stream.
     *
     * @param count The number of shorts to read.
     * @return An array of shorts, the length of which is equal to count.
     * @throws IOException Thrown if the input stream cannot produce the data.
     */
    public short[] readShort(int count) throws IOException {
        short[] shorts = new short[count];

        for (int idx = 0; idx < count; idx++) {
            shorts[idx] = readShort();
        }

        return shorts;
    }

    /**
     * Reads zero or more ints (32-bit values) from the input stream.
     *
     * @param count The number of ints to read.
     * @return An array of ints, the length of which is equal to count.
     * @throws IOException Thrown if the input stream cannot produce the data.
     */
    public int[] readInt(int count) throws IOException {
        int[] ints = new int[count];

        for (int idx = 0; idx < count; idx++) {
            ints[idx] = readInt();
        }

        return ints;
    }

    /**
     * Reads zero or more longs (64-bit values) from the input stream.
     *
     * @param count The number of longs to read.
     * @return An array of longs, the length of which is equal to count.
     * @throws IOException Thrown if the input stream cannot produce the data.
     */
    public long[] readLong(int count) throws IOException {
        long[] longs = new long[count];

        for (int idx = 0; idx < count; idx++) {
            longs[idx] = readLong();
        }

        return longs;
    }

    /**
     * Reads zero or more bytes (8-bit values) from the input stream.
     *
     * @param count The number of bytes to read.
     * @return An array of bytes, the length of which is equal to count.
     * @throws IOException Thrown if the input stream cannot produce the data.
     */
    public byte[] readBytes(int count) throws IOException {
        byte[] buffer = new byte[count];
        readFully(buffer);
        return buffer;
    }

    /**
     * Reads a fixed number of MacRoman charset-encoded characters (Pascal-style).
     *
     * @param characters The number of bytes/characters to read from the stream.
     * @return The string, in the Java-native encoding.
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
     *
     * @return The string, in the Java-native encoding.
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

    /**
     * Skips to the specified offset (from the beginning of the stream, not from the current position).
     *
     * @param offset The offset to skip to.
     * @throws IOException Thrown if the input stream cannot provide that data.
     */
    public void skipToOffset(int offset) throws IOException {
        reset();
        skipBytes(offset);
    }
}
