/**
 * ChunkUtils.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * A library of static methods used in performing chunked operations.
 */

package hypertalk.utils;

import hypertalk.ast.common.*;
import hypertalk.ast.containers.Preposition;
import hypertalk.exception.HtSemanticException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChunkUtils {

    private final static Pattern CHAR_REGEX = Pattern.compile(".");
    private final static Pattern WORD_REGEX = Pattern.compile("\\w+");
    private final static Pattern LINE_REGEX = Pattern.compile("(?m)^.*$");

    /**
     * Gets a chunk of a value. For example, 'the last line of x'
     *
     * @param c              The type of chunk; character, item, word, line or range thereof.
     * @param value          The value whose chunk is being queried
     * @param chunkNumber    For single-chunk queries, the number of the requested chunk (i.e., word 1, item 3, etc.)
     *                       counting from 1, not zero. For range-chunk queries, the first requested chunk in the range,
     *                       inclusive (i.e., words 1 to 3; chunkNumber = 1)
     * @param endChunkNumber Ignored in single-chunk queries. In range-chunk queries the last requested chunk in the
     *                       range, inclusive (i.e., lines 9 to 13; chunkNumber = 0, endChunkNumber = 13)
     * @return The requested chunk.
     */
    public static String getChunk(ChunkType c, String value, int chunkNumber, int endChunkNumber) {
        switch (c) {
            case CHAR:
                return ChunkUtils.getChar(value, chunkNumber);
            case WORD:
                return ChunkUtils.getWord(value, chunkNumber);
            case ITEM:
                return ChunkUtils.getItem(value, chunkNumber);
            case LINE:
                return ChunkUtils.getLine(value, chunkNumber);
            case CHARRANGE:
                return ChunkUtils.getCharRange(value, chunkNumber, endChunkNumber);
            case WORDRANGE:
                return ChunkUtils.getWordRange(value, chunkNumber, endChunkNumber);
            case ITEMRANGE:
                return ChunkUtils.getItemRange(value, chunkNumber, endChunkNumber);
            case LINERANGE:
                return ChunkUtils.getLineRange(value, chunkNumber, endChunkNumber);
            default:
                throw new RuntimeException("Bug! Unimplemented");
        }
    }

    /**
     * Puts a value into a chunk of another value. For example, 'put x into the first char of y'
     *
     * @param chunkType      The type of chunk; character, item, word, line or range thereof.
     * @param preposition    One of into, before or after indicating the chunk-relative position where the value should be
     *                       inserted.
     * @param mutableString  The string value whose chunk will be mutated and returned by this operation.
     * @param chunkNumber    For single-chunk queries, the number of the requested chunk (i.e., word 1, item 3, etc.)
     *                       counting from 1, not zero. For range-chunk queries, the first requested chunk in the range,
     *                       inclusive (i.e., words 1 to 3; chunkNumber = 1)
     * @param endChunkNumber Ignored in single-chunk queries. In range-chunk queries the last requested chunk in the
     *                       range, inclusive (i.e., lines 9 to 13; chunkNumber = 0, endChunkNumber = 13)
     * @param mutatorString  The string value that will be inserted into the mutableString.
     * @return The string resulting from this put operation.
     * @see #putCompositeChunk(CompositeChunk, Preposition, String, String)
     */
    public static String putChunk(ChunkType chunkType, Preposition preposition, String mutableString, int chunkNumber, int endChunkNumber, String mutatorString) {
        switch (chunkType) {
            case CHAR:
                return putChar(preposition, mutableString, chunkNumber, mutatorString);
            case WORD:
                return putWord(preposition, mutableString, chunkNumber, mutatorString);
            case ITEM:
                return putItem(preposition, mutableString, chunkNumber, mutatorString);
            case LINE:
                return putLine(preposition, mutableString, chunkNumber, mutatorString);
            case CHARRANGE:
                return putCharRange(preposition, mutableString, chunkNumber, endChunkNumber, mutatorString);
            case WORDRANGE:
                return putWordRange(preposition, mutableString, chunkNumber, endChunkNumber, mutatorString);
            case ITEMRANGE:
                return putItemRange(preposition, mutableString, chunkNumber, endChunkNumber, mutatorString);
            case LINERANGE:
                return putLineRange(preposition, mutableString, chunkNumber, endChunkNumber, mutatorString);
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }

    /**
     * Puts a value into a composite chunk of another value. For example, 'put x into the first char of the second word
     * of the third line of y'
     *
     * @param c             The composite chunk to be modified
     * @param p             One of into, before or after indicating the chunk-relative position where the value should be
     *                      inserted.
     * @param mutableString The string value whose chunk will be mutated and returned by this operation.
     * @param mutatorString The string value that will be inserted into the mutableString.
     * @return The string resulting from this put operation.
     * @throws HtSemanticException If an expression present in the composite chunk cannot be evaluated correctly.
     */
    public static String putCompositeChunk(CompositeChunk c, Preposition p, String mutableString, String mutatorString) throws HtSemanticException {
        Range s = RangeUtils.getRange(mutableString, c);

        switch (p) {
            case BEFORE:
                return insertBefore(mutableString, c.getMutatedChunkType(), s, mutatorString);
            case INTO:
                return replace(mutableString, c.getMutatedChunkType(), s, mutatorString);
            case AFTER:
                return insertAfter(mutableString, c.getMutatedChunkType(), s, mutatorString);
            default:
                throw new RuntimeException("Bug! Not implemented");
        }
    }

    /**
     * Gets the number of chunks of the specified type that exist in value.
     *
     * @param chunkType The type of chunk to count; characters, words, lines or items.
     * @param value     The value whose chunks are to be counted.
     * @return The number of found chunks
     */
    public static int getCount(ChunkType chunkType, String value) {
        Pattern pattern = getRegexForChunkType(chunkType);
        Matcher matcher = pattern.matcher(value);
        return getMatchCount(matcher);
    }

    /**
     * Gets a regular expression useful in matching "tokens" of the given ChunkType.
     *
     * @param chunkType The ChunkType whose regular expression should be returned.
     * @return The regex for the given chunk type.
     */
    public static Pattern getRegexForChunkType(ChunkType chunkType) {
        String itemDelim = Value.ITEM_DELIMITER;

        switch (chunkType) {
            case CHAR:
                return CHAR_REGEX;
            case WORD:
                return WORD_REGEX;
            case LINE:
                return LINE_REGEX;
            case ITEM:
                StringBuilder patternBuilder = new StringBuilder();

                // Match empty item in first position (i.e., ",2,3" -- item 3 is '3')
                patternBuilder.append("^(?=").append(itemDelim).append(")|");

                // Match empty item in last position (i.e., "1,2,3," -- item 4 is '')
                patternBuilder.append("(?<=").append(itemDelim).append(")$|");

                // Match empty item mid-list (i.e., "1,,2,3" -- item 2 is '')
                patternBuilder.append("(?<=").append(itemDelim).append(")(?=").append(itemDelim).append(")|");

                // Normal case: Match all non-delimiter characters between delimiters (i.e., "1,2,3" -- item 2 is '2')
                patternBuilder.append("[^").append(itemDelim).append("]+");

                return Pattern.compile(patternBuilder.toString());
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }

    protected static String putChar(Preposition p, String value, int charIdx, String replacement) {
        switch (p) {
            case BEFORE:
                return insertBefore(value, ChunkType.CHAR, charIdx, replacement);
            case INTO:
                return replace(value, ChunkType.CHAR, charIdx, charIdx, replacement);
            case AFTER:
                return insertAfter(value, ChunkType.CHAR, charIdx, replacement);
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }

    protected static String putCharRange(Preposition p, String value, int start, int end, String replacement) {
        switch (p) {
            case BEFORE:
                return replace(value, ChunkType.CHAR, start - 1, end, replacement);
            case INTO:
                return replace(value, ChunkType.CHAR, start, end, replacement);
            case AFTER:
                return replace(value, ChunkType.CHAR, start, end + 1, replacement);
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }

    protected static String putWord(Preposition p, String value, int wordIdx, String replacement) {
        switch (p) {
            case BEFORE:
                return insertBefore(value, ChunkType.WORD, wordIdx, replacement);
            case INTO:
                return replace(value, ChunkType.WORD, wordIdx, wordIdx, replacement);
            case AFTER:
                return insertAfter(value, ChunkType.WORD, wordIdx, replacement);
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }

    protected static String putWordRange(Preposition p, String value, int start, int end, String replacement) {
        switch (p) {
            case BEFORE:
                return replace(value, ChunkType.WORD, start - 1, end, replacement);
            case INTO:
                return replace(value, ChunkType.WORD, start, end, replacement);
            case AFTER:
                return replace(value, ChunkType.WORD, start, end + 1, replacement);
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }

    protected static String putItem(Preposition p, String value, int itemIdx, String replacement) {
        switch (p) {
            case BEFORE:
                return insertBefore(value, ChunkType.ITEM, itemIdx, replacement);
            case INTO:
                return replace(value, ChunkType.ITEM, itemIdx, itemIdx, replacement);
            case AFTER:
                return insertAfter(value, ChunkType.ITEM, itemIdx, replacement);
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }

    protected static String putItemRange(Preposition p, String value, int start, int end, String replacement) {
        switch (p) {
            case BEFORE:
                return replace(value, ChunkType.ITEM, start - 1, end, replacement);
            case INTO:
                return replace(value, ChunkType.ITEM, start, end, replacement);
            case AFTER:
                return replace(value, ChunkType.ITEM, start, end + 1, replacement);
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }

    protected static String putLine(Preposition p, String value, int lineIdx, String replacement) {
        switch (p) {
            case BEFORE:
                return insertBefore(value, ChunkType.LINE, lineIdx, replacement);
            case INTO:
                return replace(value, ChunkType.LINE, lineIdx, lineIdx, replacement);
            case AFTER:
                return insertAfter(value, ChunkType.LINE, lineIdx, replacement);
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }

    protected static String putLineRange(Preposition p, String value, int start, int end, String replacement) {
        switch (p) {
            case BEFORE:
                return replace(value, ChunkType.LINE, start - 1, end, replacement);
            case INTO:
                return replace(value, ChunkType.LINE, start, end, replacement);
            case AFTER:
                return replace(value, ChunkType.LINE, start, end + 1, replacement);
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }

    protected static String getChar(String value, int charIdx) {
        return get(value, ChunkType.CHAR, charIdx);
    }

    protected static String getCharRange(String value, int start, int end) {
        return get(value, ChunkType.CHAR, start, end);
    }

    protected static String getWord(String value, int wordIdx) {
        return get(value, ChunkType.WORD, wordIdx);
    }

    protected static String getWordRange(String value, int start, int end) {
        return get(value, ChunkType.WORD, start, end);
    }

    protected static String getItem(String value, int itemIdx) {
        return get(value, ChunkType.ITEM, itemIdx);
    }

    protected static String getItemRange(String value, int start, int end) {
        return get(value, ChunkType.ITEM, start, end);
    }

    protected static String getLine(String value, int lineIdx) {
        return get(value, ChunkType.LINE, lineIdx);
    }

    protected static String getLineRange(String value, int start, int end) {
        return get(value, ChunkType.LINE, start, end);
    }

    private static String insertBefore(String value, ChunkType delimiter, Range range, String replacement) {
        return value.substring(0, range.start) + replacement + getSeparatorForChunkType(delimiter) + value.substring(range.start);
    }

    private static String insertBefore(String value, ChunkType delimiter, int index, String replacement) {
        Range range = RangeUtils.getRange(value, delimiter, index);
        return value.substring(0, range.start) + replacement + getSeparatorForChunkType(delimiter) + value.substring(range.start);
    }

    private static String insertAfter(String value, ChunkType delimiter, Range range, String replacement) {
        return value.substring(0, range.end) + getSeparatorForChunkType(delimiter) + replacement + value.substring(range.end);
    }

    private static String insertAfter(String value, ChunkType delimiter, int index, String replacement) {
        Range range = RangeUtils.getRange(value, delimiter, index);
        return value.substring(0, range.end) + getSeparatorForChunkType(delimiter) + replacement + value.substring(range.end);
    }

    private static String replace(String value, ChunkType delimiter, Range range, String replacement) {
        return value.substring(0, range.start) + replacement + value.substring(range.end);
    }

    private static String replace(String value, ChunkType delimiter, int start, int end, String replacement) {
        Range range = RangeUtils.getRange(value, delimiter, start, end);
        return value.substring(0, range.start) + replacement + value.substring(range.end);
    }

    private static String get(String value, ChunkType chunkType, int startIndex, int endIndex) {
        Range range = RangeUtils.getRange(value, chunkType, startIndex, endIndex);
        return value.substring(range.start, range.end);
    }

    private static String get(String value, ChunkType chunkType, int index) {
        Range range = RangeUtils.getRange(value, chunkType, index);
        return value.substring(range.start, range.end);
    }

    static int getMatchCount(Matcher matcher) {
        int matchCount = 0;
        while (matcher.find()) {
            matchCount++;
        }
        matcher.reset();
        return matchCount;
    }

    private static String getSeparatorForChunkType(ChunkType chunkType) {
        switch (chunkType) {
            case CHAR:
                return "";
            case WORD:
                return " ";
            case LINE:
                return "\n";
            case ITEM:
                return Value.ITEM_DELIMITER;
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }
}
