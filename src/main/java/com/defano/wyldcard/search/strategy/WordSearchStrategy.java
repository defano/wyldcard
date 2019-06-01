package com.defano.wyldcard.search.strategy;

import com.defano.hypertalk.util.Range;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds whole words in the searchable text. Search term should not contain any whitespace (if it is expected to match
 * any text). Only whole words will match; substrings contained within a word will not. Use {@link WholeSearchStrategy}
 * to find substrings.
 */
public class WordSearchStrategy implements SearchStrategy {

    private static final Pattern words = Pattern.compile("\\w+");

    @Override
    public Range search(String text, String term, int searchFrom) {
        String searchText = text.substring(searchFrom);
        Matcher matcher = words.matcher(searchText);

        while (matcher.find()) {
            String word = searchText.substring(matcher.start(), matcher.end());
            if (word.equalsIgnoreCase(term)) {
                return new Range(searchFrom + matcher.start(), searchFrom + matcher.end());
            }
        }

        return null;
    }
}