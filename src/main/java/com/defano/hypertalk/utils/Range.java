package com.defano.hypertalk.utils;

import com.defano.hypertalk.ast.model.chunk.Chunk;
import com.defano.hypertalk.ast.model.chunk.ChunkType;
import com.defano.hypertalk.ast.expressions.LiteralExp;

public class Range {

    public final int start, end;

    /**
     * Constructs an empty range of zero-length;
     */
    public Range() {
        this(0, 0);
    }

    /**
     * Constructs a range from start and end character index. Note that {@link #start} will always be the lesser of
     * start and end.
     *
     * @param start The first character in the range, counting from 0, inclusive.
     * @param end   The last character in the range, counting from 0, exclusive.
     */
    public Range(int start, int end) {
        this.start = start < end ? start : end;
        this.end = start < end ? end : start;
    }

    public static Range ofMarkAndDot(int dot, int mark) {
        return new Range(dot, mark);
    }

    /**
     * Constructs a range from a start and inclusive end character index.
     *
     * @param start The first character in the range, counting from 0, inclusive.
     * @param end   The last character in the range, counting from 0, inclusive.
     * @return The Range
     */
    public static Range inclusive(int start, int end) {
        int lesser = start < end ? start : end;
        int greater = start < end ? end : start;

        return new Range(lesser, greater + 1);
    }

    /**
     * Gets the number of characters encompassed by this range.
     *
     * @return The length of the range.
     */
    public int length() {
        return end - start;
    }

    /**
     * Gets a HyperTalk character range chunk specifier identifying the characters in this range.
     *
     * @return A HyperTalk chunk specifier.
     */
    public Chunk asChunk() {
        return new Chunk(ChunkType.CHARRANGE, new LiteralExp(null, start + 1), new LiteralExp(null, end));
    }

    /**
     * Determines if this range represents an empty selection
     *
     * @return True if the range is empty
     */
    public boolean isEmpty() {
        return length() < 1;
    }

    @Override
    public String toString() {
        return "Range{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
