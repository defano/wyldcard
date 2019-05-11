package com.defano.wyldcard.search.strategy;

import com.defano.hypertalk.util.Range;

public interface SearchStrategy {

    /**
     * Search the text starting starting from a given offset for a given search term.
     *
     * @param text The text to search
     * @param term The term to find in the search text
     * @param searchFrom The first character (inclusive) from where to begin searching the search text.
     * @return Null if the given search term is not found, or a range of characters indicating the where
     * in the text the search term was found.
     */
    Range search(String text, String term, int searchFrom);
}
