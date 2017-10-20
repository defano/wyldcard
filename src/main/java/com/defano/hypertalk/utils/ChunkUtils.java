package com.defano.hypertalk.utils;

import com.defano.hypercard.runtime.context.HyperCardProperties;
import com.defano.hypertalk.ast.common.ChunkType;
import com.defano.hypertalk.ast.common.CompositeChunk;
import com.defano.hypertalk.ast.common.Ordinal;
import com.defano.hypertalk.exception.HtException;
import com.google.common.collect.Lists;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChunkUtils {

    private final static Pattern CHAR_REGEX = Pattern.compile("[\\s\\S]");
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
     *                       range, inclusive (i.e., lines 9 to 13; chunkNumber = 9, endChunkNumber = 13)
     * @return The requested chunk.
     */
    public static String getChunk(ChunkType c, String value, int chunkNumber, int endChunkNumber) {
        Range range;

        if (c.isRange()) {
            range = RangeUtils.getRange(value, c, chunkNumber, endChunkNumber);
        } else {
            range = RangeUtils.getRange(value, c, chunkNumber);
        }

        return value.substring(range.start, range.end);
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
     *                       range, inclusive (i.e., lines 9 to 13; chunkNumber = 9, endChunkNumber = 13)
     * @param mutatorString  The string value that will be inserted into the mutableString.
     * @return The string resulting from this put operation.
     * @see #putCompositeChunk(CompositeChunk, Preposition, String, String)
     */
    public static String putChunk(ChunkType chunkType, Preposition preposition, String mutableString, int chunkNumber, int endChunkNumber, String mutatorString) {

        if (chunkNumber != Ordinal.LAST.intValue() && chunkNumber != Ordinal.MIDDLE.intValue()) {

            int chunksInContainer = getCount(chunkType, mutableString);

            // Disallow mutating non-existent word/character chunks (lines/items are okay)
            if ((chunkType == ChunkType.WORD || chunkType == ChunkType.CHAR) && chunksInContainer < chunkNumber) {
                throw new IllegalArgumentException("Cannot set " + chunkType + " " + chunksInContainer + " because it doesn't exist.");
            }

            // If necessary, add as many lines/items as are needed to assure the value can be mutated
            String separator = getSeparatorForChunkType(chunkType);
            StringBuilder mutableStringBuilder = new StringBuilder(mutableString);
            for (int index = chunksInContainer; index < chunkNumber; index++) {
                mutableStringBuilder.append(separator);
            }
            mutableString = mutableStringBuilder.toString();
        }

        if (chunkType.isRange()) {
            return putChunkRange(chunkType, preposition, mutableString, chunkNumber, endChunkNumber, mutatorString);
        } else {
            return putSingleChunk(chunkType, preposition, mutableString, chunkNumber, mutatorString);
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
    public static String putCompositeChunk(CompositeChunk c, Preposition p, String mutableString, String mutatorString) throws HtException {
        Range s = RangeUtils.getRange(mutableString, c);

        switch (p) {
            case BEFORE:
                return insertBefore(mutableString, c.getMutatedChunkType(), s, mutatorString);
            case INTO:
                return replace(mutableString, s, mutatorString);
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
     * Gets a regular expression useful in matching tokens of the given ChunkType.
     *
     * @param chunkType The ChunkType whose regular expression should be returned.
     * @return The regex for the given chunk type.
     */
    static Pattern getRegexForChunkType(ChunkType chunkType) {

        switch (chunkType) {
            case CHAR:
            case CHARRANGE:
                return CHAR_REGEX;
            case WORD:
            case WORDRANGE:
                return WORD_REGEX;
            case LINE:
            case LINERANGE:
                return LINE_REGEX;
            case ITEM:
            case ITEMRANGE:
                String itemDelimiterRegex = getItemDelimiterRegex();
                StringBuilder patternBuilder = new StringBuilder();

                // Match empty item in first position (i.e., ",2,3" -- item 3 is '3')
                patternBuilder.append("^(?=").append(itemDelimiterRegex).append(")|");

                // Match empty item in last position (i.e., "1,2,3," -- item 4 is '')
                patternBuilder.append("(?<=").append(itemDelimiterRegex).append(")$|");

                // Match empty item mid-list (i.e., "1,,2,3" -- item 2 is '')
                patternBuilder.append("(?<=").append(itemDelimiterRegex).append(")(?=").append(itemDelimiterRegex).append(")|");

                // Normal case: Match all non-delimiter characters between delimiters (i.e., "1,2,3" -- item 2 is '2')
                patternBuilder.append("[^").append(itemDelimiterRegex).append("]+");

                return Pattern.compile(patternBuilder.toString());

            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }

    /**
     * Converts the item delimiter string (which may contain regex special characters) into a valid regular expression
     * by pre-pending special characters with an escape '\'.
     *
     * @return A valid regular expression matching strings that are equal to item delimiter string literal.
     */
    private static String getItemDelimiterRegex() {
        List<Character> specialChars = Lists.charactersOf("[\\^$.|?*+()");

        String itemDelimiter = ExecutionContext.getContext().getGlobalProperties().getKnownProperty(HyperCardProperties.PROP_ITEMDELIMITER).stringValue();
        StringBuilder itemDelimiterRegex = new StringBuilder();

        for (char thisChar : itemDelimiter.toCharArray()) {
            if (specialChars.contains(thisChar)) {
                itemDelimiterRegex.append("\\");
            }

            itemDelimiterRegex.append(thisChar);
        }

        return itemDelimiterRegex.toString();
    }

    /**
     * When mutating a chunk, this method determines the "separator" that should be inserted between chunks. For
     * example, a single space between words.
     *
     * @param chunkType
     * @return
     */
    private static String getSeparatorForChunkType(ChunkType chunkType) {
        switch (chunkType) {
            case CHAR:
            case CHARRANGE:
                return "";
            case WORDRANGE:
            case WORD:
                return " ";
            case LINERANGE:
            case LINE:
                return "\n";
            case ITEMRANGE:
            case ITEM:
                return ExecutionContext.getContext().getGlobalProperties().getKnownProperty(HyperCardProperties.PROP_ITEMDELIMITER).stringValue();
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }

    /**
     * Returns the number of pattern matches present in the given reg-ex matcher. Note that this is not the same
     * as the group count and cannot be retrieved with match.groupCount().
     *
     * @param matcher The regex matcher to count.
     * @return
     */
    static int getMatchCount(Matcher matcher) {
        int matchCount = 0;
        while (matcher.find()) {
            matchCount++;
        }
        matcher.reset();
        return matchCount;
    }

    private static String putSingleChunk(ChunkType c, Preposition p, String value, int start, String replacement) {
        switch (p) {
            case BEFORE:
                return insertBefore(value, c, start, replacement);
            case INTO:
                return replace(value, c, start, start, replacement);
            case AFTER:
                return insertAfter(value, c, start, replacement);
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
    }

    private static String putChunkRange(ChunkType c, Preposition p, String value, int start, int end, String replacement) {
        switch (p) {
            case BEFORE:
                return replace(value, c, start - 1, end, replacement);
            case INTO:
                return replace(value, c, start, end, replacement);
            case AFTER:
                return replace(value, c, start, end + 1, replacement);
            default:
                throw new RuntimeException("Bug! Not implemented.");
        }
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

    private static String replace(String value, Range range, String replacement) {
        return value.substring(0, range.start) + replacement + value.substring(range.end);
    }

    private static String replace(String value, ChunkType delimiter, int start, int end, String replacement) {
        Range range = RangeUtils.getRange(value, delimiter, start, end);
        return value.substring(0, range.start) + replacement + value.substring(range.end);
    }
}
