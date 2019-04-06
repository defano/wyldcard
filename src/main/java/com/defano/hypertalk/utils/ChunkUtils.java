package com.defano.hypertalk.utils;

import com.defano.hypertalk.ast.model.Ordinal;
import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.chunk.ChunkType;
import com.defano.hypertalk.ast.model.chunk.CompositeChunk;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.wyldcard.WyldCardProperties;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChunkUtils {

    private final static Pattern CHAR_REGEX = Pattern.compile("[\\s\\S]");
    private final static Pattern WORD_REGEX = Pattern.compile("\\S+");
    private final static Pattern LINE_REGEX = Pattern.compile("(?m)^.*$");

    /**
     * Gets a chunk of a value. For example, 'the last line of x'
     *
     *
     * @param context The execution context.
     * @param c              The type of chunk; character, item, word, line or range thereof.
     * @param value          The value whose chunk is being queried
     * @param chunkNumber    For single-chunk queries, the number of the requested chunk (i.e., word 1, item 3, etc.)
     *                       counting from 1, not zero. For range-chunk queries, the first requested chunk in the range,
     *                       inclusive (i.e., words 1 to 3; chunkNumber = 1)
     * @param endChunkNumber Ignored in single-chunk queries. In range-chunk queries the last requested chunk in the
     *                       range, inclusive (i.e., lines 9 to 13; chunkNumber = 9, endChunkNumber = 13)
     * @return The requested chunk.
     */
    public static String getChunk(ExecutionContext context, ChunkType c, String value, int chunkNumber, int endChunkNumber) {
        Range range;

        if (c.isRange()) {
            range = RangeUtils.getRange(context, value, c, chunkNumber, endChunkNumber);
        } else {
            range = RangeUtils.getRange(context, value, c, chunkNumber);
        }

        return value.substring(range.start, range.end);
    }

    /**
     * Puts a value into a chunk of another value. For example, 'put x into the first char of y'
     *
     *
     * @param context The execution context.
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
     * @see #putCompositeChunk(ExecutionContext, CompositeChunk, Preposition, String, String)
     */
    public static String putChunk(ExecutionContext context, ChunkType chunkType, Preposition preposition, String mutableString, int chunkNumber, int endChunkNumber, String mutatorString) throws HtSemanticException {

        if (!Ordinal.isReservedValue(chunkNumber)) {

            int chunksInContainer = getCount(context, chunkType, mutableString);

            // Disallow mutating non-existent word/character chunks (lines/items are okay)
            if ((chunkType.isWordChunk() || chunkType.isCharChunk()) && (chunksInContainer < chunkNumber || (chunkType.isRange() && chunksInContainer < endChunkNumber)))
            {
                throw new HtSemanticException("That chunk doesn't exist.");
            }

            // If necessary, add as many lines/items as are needed to assure the value can be mutated
            String separator = getSeparatorForChunkType(context, chunkType);
            StringBuilder mutableStringBuilder = new StringBuilder(mutableString);
            for (int index = chunksInContainer; index < chunkNumber; index++) {
                mutableStringBuilder.append(separator);
            }
            mutableString = mutableStringBuilder.toString();
        }

        // HyperCard disallows range chunk mutations; we're okay with 'em
        if (chunkType.isRange()) {
            return putChunkRange(context, chunkType, preposition, mutableString, chunkNumber, endChunkNumber, mutatorString);
        } else {
            return putSingleChunk(context, chunkType, preposition, mutableString, chunkNumber, mutatorString);
        }
    }

    /**
     * Puts a value into a composite chunk of another value. For example, 'put x into the first char of the second word
     * of the third line of y'
     *
     *
     * @param context The execution context.
     * @param c             The composite chunk to be modified
     * @param p             One of into, before or after indicating the chunk-relative position where the value should be
     *                      inserted.
     * @param mutableString The string value whose chunk will be mutated and returned by this operation.
     * @param mutatorString The string value that will be inserted into the mutableString.
     * @return The string resulting from this put operation.
     * @throws HtSemanticException If an expression present in the composite chunk cannot be evaluated correctly.
     */
    public static String putCompositeChunk(ExecutionContext context, CompositeChunk c, Preposition p, String mutableString, String mutatorString) throws HtException {
        Range s = RangeUtils.getRange(context, mutableString, c);

        switch (p) {
            case BEFORE:
                return insertBefore(context, mutableString, c.getMutatedChunkType(), s, mutatorString);
            case INTO:
                return insertInto(context, mutableString, s, mutatorString);
            case AFTER:
                return insertAfter(context, mutableString, c.getMutatedChunkType(), s, mutatorString);
            case REPLACING:
                return replace(context, mutableString, c.getMutatedChunkType(), s.start + 1, s.end, mutatorString);
            default:
                throw new RuntimeException("Bug! Not implemented: " + p);
        }
    }

    /**
     * Gets the number of chunks of the specified type that exist in value.
     *
     *
     * @param context The execution context.
     * @param chunkType The type of chunk to count; characters, words, lines or items.
     * @param value     The value whose chunks are to be counted.
     * @return The number of found chunks
     */
    public static int getCount(ExecutionContext context, ChunkType chunkType, String value) {
        Pattern pattern = getRegexForChunkType(context, chunkType);
        Matcher matcher = pattern.matcher(value);
        return getMatchCount(matcher);
    }

    /**
     * Gets a regular expression useful in matching tokens of the given ChunkType.
     *
     *
     * @param context The execution context.
     * @param chunkType The ChunkType whose regular expression should be returned.
     * @return The regex for the given chunk type.
     */
    public static Pattern getRegexForChunkType(ExecutionContext context, ChunkType chunkType) {

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
                String itemDelimiterRegex = getItemDelimiterRegex(context);
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
                throw new RuntimeException("Bug! Not implemented: " + chunkType);
        }
    }

    /**
     * Converts the item delimiter string (which may contain regex special characters) into a valid regular expression
     * by pre-pending special characters with an escape '\'.
     *
     * @return A valid regular expression matching strings that are equal to item delimiter string literal.
     * @param context The execution context.
     */
    private static String getItemDelimiterRegex(ExecutionContext context) {
        List<Character> specialChars = Lists.charactersOf("[\\^$.|?*+()");

        String itemDelimiter = WyldCard.getInstance().getWyldCardPart().getKnownProperty(context, WyldCardProperties.PROP_ITEMDELIMITER).toString();
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
     *
     * @param context The execution context.
     * @param chunkType
     * @return
     */
    private static String getSeparatorForChunkType(ExecutionContext context, ChunkType chunkType) {
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
                return WyldCard.getInstance().getWyldCardPart().getKnownProperty(context, WyldCardProperties.PROP_ITEMDELIMITER).toString();
            default:
                throw new RuntimeException("Bug! Not implemented: " + chunkType);
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

    private static String putSingleChunk(ExecutionContext context, ChunkType c, Preposition p, String value, int start, String replacement) {
        switch (p) {
            case BEFORE:
                return insertBefore(context, value, c, start, replacement);
            case INTO:
                return insertInto(context, value, c, start, start, replacement);
            case AFTER:
                return insertAfter(context, value, c, start, replacement);
            case REPLACING:
                return replace(context, value, c, start, start, replacement);
            default:
                throw new RuntimeException("Bug! Not implemented: " + p);
        }
    }

    private static String putChunkRange(ExecutionContext context, ChunkType c, Preposition p, String value, int start, int end, String replacement) {
        switch (p) {
            case BEFORE:
                return insertInto(context, value, c, start - 1, end, replacement);
            case INTO:
                return insertInto(context, value, c, start, end, replacement);
            case AFTER:
                return insertInto(context, value, c, start, end + 1, replacement);
            case REPLACING:
                return replace(context, value, c, start, end, replacement);
            default:
                throw new RuntimeException("Bug! Not implemented: " + p);
        }
    }

    private static String insertBefore(ExecutionContext context, String value, ChunkType delimiter, Range range, String replacement) {
        return value.substring(0, range.start) + replacement + getSeparatorForChunkType(context, delimiter) + value.substring(range.start);
    }

    private static String insertBefore(ExecutionContext context, String value, ChunkType delimiter, int index, String replacement) {
        Range range = RangeUtils.getRange(context, value, delimiter, index);
        return value.substring(0, range.start) + replacement + getSeparatorForChunkType(context, delimiter) + value.substring(range.start);
    }

    private static String insertAfter(ExecutionContext context, String value, ChunkType delimiter, Range range, String replacement) {
        return value.substring(0, range.end) + getSeparatorForChunkType(context, delimiter) + replacement + value.substring(range.end);
    }

    private static String insertAfter(ExecutionContext context, String value, ChunkType delimiter, int index, String replacement) {
        Range range = RangeUtils.getRange(context, value, delimiter, index);
        return value.substring(0, range.end) + getSeparatorForChunkType(context, delimiter) + replacement + value.substring(range.end);
    }

    private static String insertInto(ExecutionContext context, String value, Range range, String replacement) {
        return value.substring(0, range.start) + replacement + value.substring(range.end);
    }

    private static String insertInto(ExecutionContext context, String value, ChunkType delimiter, int start, int end, String replacement) {
        Range range = RangeUtils.getRange(context, value, delimiter, start, end);
        return value.substring(0, range.start) + replacement + value.substring(range.end);
    }

    private static String replace(ExecutionContext context, String value, ChunkType delimiter, int start, int end, String replacement) {
        Range range = RangeUtils.getRange(context, value, delimiter, start, end);

        int startChar = range.start;
        int endChar = range.end;

        if (start > 1) {
            startChar -= getSeparatorForChunkType(context, delimiter).length();
            startChar = Math.max(0, startChar);
        } else {
            endChar += getSeparatorForChunkType(context, delimiter).length();
            endChar = Math.min(endChar, value.length());
        }

        return value.substring(0, startChar) + replacement + value.substring(endChar);
    }

}
