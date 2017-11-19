package com.defano.hypercard.search;

import com.defano.hypercard.search.strategy.CharsSearchStrategy;
import com.defano.hypercard.search.strategy.SearchStrategy;
import com.defano.hypercard.search.strategy.WordsSearchStrategy;
import com.defano.hypertalk.ast.common.SearchType;

public class SearchFactory {

    public static SearchStrategy searchBy(SearchType type) {
        switch (type) {
            case WORDS:
            case WHOLE:
                return new WordsSearchStrategy();

            case CHARS:
            case STRING:
            default:
                return new CharsSearchStrategy();

        }
    }

}
