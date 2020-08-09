package com.defano.wyldcard.search.strategy;

import com.defano.hypertalk.util.Range;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds a substring that starts at the beginning of a word. Search term may contain whitespace, and search results may
 * cross word boundaries (but will always start at a word boundary).
 */
public class WholeSearchStrategy implements SearchStrategy {

    private static final Pattern words = Pattern.compile("\\w+");

    /** {@inheritDoc} */
    @Override
    public Range search(String text, String term, int searchFrom) {
        String searchTextLower = text.substring(searchFrom).toLowerCase();
        String searchTermLower = term.toLowerCase();
        Matcher matcher = words.matcher(searchTextLower);

        while (matcher.find()) {
            String searchTextWord = searchTextLower.substring(matcher.start());
            if (searchTextWord.startsWith(searchTermLower)) {
                int foundAt = searchTextWord.indexOf(searchTermLower);
                return new Range(searchFrom + matcher.start() + foundAt, searchFrom + matcher.start() + foundAt + term.length());
            }
        }

        return null;
    }
}