package com.defano.wyldcard.search.strategy;

import com.defano.hypertalk.utils.Range;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds a substring that occurs entirely within the bounds of a word. Search term should not include whitespace (if it
 * is expected to match anything).
 */
public class CharsSearchStrategy implements SearchStrategy {

    private final static Pattern words = Pattern.compile("\\w+");

    @Override
    public Range search(String text, String term, int searchFrom) {
        String searchTextLower = text.substring(searchFrom).toLowerCase();
        String termTextLower = term.trim().toLowerCase();
        Matcher matcher = words.matcher(searchTextLower);

        while (matcher.find()) {
            String word = searchTextLower.substring(matcher.start(), matcher.end());

            if (word.contains(termTextLower)) {
                int foundAt = word.indexOf(termTextLower);
                return new Range(searchFrom + matcher.start() + foundAt, searchFrom + matcher.start() + foundAt + term.length());
            }
        }

        return null;
    }
}