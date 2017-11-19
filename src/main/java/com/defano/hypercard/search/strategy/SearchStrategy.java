package com.defano.hypercard.search.strategy;

import com.defano.hypertalk.utils.Range;

public interface SearchStrategy {

    /**
     * Search the text starting from a given offset for a given search term.
     *
     * @param text The text to search
     * @param term The text to find
     * @param searchFrom The first character (inclusive) to being searching.
     * @return Null if the given search term is not found in the text, or a range of characters indicating the where
     * in the text the search term was found.
     */
    Range search(String text, String term, int searchFrom);
}
