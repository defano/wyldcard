package com.defano.hypercard.search.strategy;

import com.defano.hypercard.search.SearchStrategy;
import com.defano.hypertalk.utils.Range;

/**
 * Search strategy that attempts to find the first case-insensitive occurrence of a search term that begins a word in
 * the text. For example, searching for "on" would match "ontology" but not "radon".
 */
public class WordsSearchStrategy implements SearchStrategy {

    /** {@inheritDoc} */
    @Override
    public Range search(String text, String term, int searchFrom) {

        String lowercaseTerm = term.toLowerCase();
        String lowercaseText = text.toLowerCase();

        int start = 0;
        boolean wordStart = true;
        boolean hit = false;

        for (int index = searchFrom; index < text.length(); index++) {
            char thisChar = lowercaseText.charAt(index);
            if (Character.isWhitespace(thisChar)) {
                wordStart = true;

                if (hit) {
                    return new Range(start, index);
                } else {
                    start = index + 1;
                }

            } else if (wordStart && lowercaseText.substring(index).startsWith(lowercaseTerm)) {
                hit = true;
            } else {
                wordStart = false;
            }
        }

        return null;
    }
}
