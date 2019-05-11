package com.defano.wyldcard.search.strategy;

import com.defano.hypertalk.util.Range;

/**
 * Finds a substring occurring anywhere in the searchable text. Search term may including whitespace, and found text may
 * cross word boundaries.
 */
public class StringSearchStrategy implements SearchStrategy {

    /** {@inheritDoc} */
    @Override
    public Range search(String text, String term, int searchFrom) {
        int start = text.toLowerCase().substring(searchFrom).indexOf(term.toLowerCase());

        if (start < 0) {
            return null;
        } else {
            return new Range(searchFrom + start, searchFrom + start + term.length());
        }
    }
}