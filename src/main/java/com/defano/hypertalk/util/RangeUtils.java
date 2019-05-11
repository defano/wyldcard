package com.defano.hypertalk.util;

import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.ast.model.chunk.Chunk;
import com.defano.hypertalk.ast.model.chunk.ChunkType;
import com.defano.hypertalk.ast.model.chunk.CompositeChunk;
import com.defano.hypertalk.ast.model.enums.Ordinal;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RangeUtils {

    /**
     * Gets the range of characters identified by the given CompositeChunk.
     *
     *
     * @param context The execution context.
     * @param value The value whose CompositeChunk should be ranged
     * @param c     The chunk specifier whose character range should be calculated
     * @return The range of characters in value identified by c.
     * @throws HtSemanticException If an expression in the composite chunk cannot be correctly evaluated.
     */
    public static Range getRange(ExecutionContext context, String value, CompositeChunk c) throws HtException {
        return getRange(context, value, c, new Range(0, value.length()));
    }

    /**
     * Gets the range of characters identified by a set of chunks.
     *
     *
     * @param context The execution context.
     * @param value     The value whose chunk should be ranged.
     * @param chunkType The type of chunk; character, item, word, line or range thereof.
     * @param start     The first requested chunk, inclusive, counting from 1.
     * @param end       The last requested chunk, inclusive, counting from 1.
     * @return The range of characters identified.
     */
    public static Range getRange(ExecutionContext context, String value, ChunkType chunkType, int start, int end) {
        Range startRange = getRange(context, value, chunkType, start);
        Range endRange = getRange(context, value, chunkType, end);
        return new Range(startRange.start, endRange.end);
    }

    /**
     * Gets the range of characters identified by the given chunk.
     *
     *
     * @param context The execution context.
     * @param value     The value whose chunk should be ranged.
     * @param chunkType The type of chunk; character, item, word, line or range thereof.
     * @param count     The first requested chunk, inclusive, counting from 1.
     * @return
     */
    public static Range getRange(ExecutionContext context, String value, ChunkType chunkType, int count) {
        Pattern pattern = ChunkUtils.getRegexForChunkType(context, chunkType);
        Matcher matcher = pattern.matcher(value);
        int matchCount = ChunkUtils.getMatchCount(matcher);

        if (count == Ordinal.LAST.intValue()) {
            advanceToMatch(matcher, matchCount - 1);
        } else if (count == Ordinal.MIDDLE.intValue()) {
            advanceToMatch(matcher, matchCount / 2);
        } else if (count == Ordinal.ANY.intValue() && matchCount > 0) {
            advanceToMatch(matcher, new Random().nextInt(matchCount));
        } else {
            advanceToMatch(matcher, count - 1);
        }

        try {
            return new Range(matcher.start(), matcher.end());
        } catch (IllegalStateException e) {
            return new Range(value.length(), value.length());
        }
    }

    /**
     * Gets the range of characters identified by a given chunk.
     *
     *
     * @param context The execution context.
     * @param value The string whose characters should be ranged
     * @param c The chunk identifying a substring (i.e., 'second word of', 'third item of')
     * @return The range of identified characters.
     * @throws HtSemanticException Thrown if the chunk is invalid.
     */
    public static Range getRange(ExecutionContext context, String value, Chunk c) throws HtException {
        Value startVal = null;
        Value endVal = null;

        if (c.start != null)
            startVal = c.start.evaluate(context);
        if (c.end != null)
            endVal = c.end.evaluate(context);

        if (!startVal.isNatural() && !Ordinal.isReservedValue(startVal.integerValue()))
            throw new HtSemanticException("Chunk specifier requires natural integer value, got '" + startVal + "' instead");
        if (endVal != null && !endVal.isNatural() && !Ordinal.isReservedValue(endVal.integerValue()))
            throw new HtSemanticException("Chunk specifier requires natural integer value, got '" + endVal + "' instead");

        if (endVal != null)
            return getRange(context, value, c.type, startVal.integerValue(), endVal.integerValue());
        else
            return getRange(context, value, c.type, startVal.integerValue());
    }

    private static Range getRange(ExecutionContext context, String value, CompositeChunk c, Range in) throws HtException {
        Range s = getRange(context, value, (Chunk) c, in);

        value = new Value(value).getChunk(context, new Chunk(c.type, c.start, c.end)).toString();
        s = getRange(context, value, c.chunkOf, s);

        if (c.chunkOf instanceof CompositeChunk) {
            Chunk next = ((CompositeChunk) c.chunkOf).chunkOf;
            value = new Value(value).getChunk(context, new Chunk(c.chunkOf.type, c.chunkOf.start, c.chunkOf.end)).toString();

            if (next instanceof CompositeChunk) {
                return getRange(context, value, (CompositeChunk) next, s);
            } else {
                return getRange(context, value, next, s);
            }
        } else {
            return s;
        }
    }


    private static Range getRange(ExecutionContext context, String value, Chunk c, Range in) throws HtException {
        Range range = getRange(context, value, c);
        return new Range(in.start + range.start, in.start + range.start + (range.end - range.start));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void advanceToMatch(Matcher matcher, int index) {
        while (index-- >= 0) {
            matcher.find();
        }
    }

}
