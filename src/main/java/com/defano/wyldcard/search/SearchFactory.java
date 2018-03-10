package com.defano.wyldcard.search;

import com.defano.wyldcard.search.strategy.*;
import com.defano.hypertalk.ast.model.SearchType;

public class SearchFactory {

    public static SearchStrategy searchBy(SearchType type) {
        switch (type) {
            case WORDS:
                return new WordSearchStrategy();

            case WHOLE:
                return new WholeSearchStrategy();

            case CHARS:
                return new CharsSearchStrategy();

            case STRING:
                return new StringSearchStrategy();

            default:
                throw new IllegalStateException("Bug! Unimplemented search type: " + type);
        }
    }

}
