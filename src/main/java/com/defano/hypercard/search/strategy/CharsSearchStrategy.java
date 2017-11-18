package com.defano.hypercard.search.strategy;

import com.defano.hypercard.search.SearchStrategy;
import com.defano.hypertalk.utils.Range;

/**
 * Searches text for the
 */
public class CharsSearchStrategy implements SearchStrategy {

    /** {@inheritDoc} */
    @Override
    public Range search(String text, String term, int searchFrom) {
        int start = text.toLowerCase().substring(searchFrom).indexOf(term.toLowerCase());

        if (start < 0) {
            return null;
        } else {
            return new Range(start, start + term.length());
        }
    }
}
