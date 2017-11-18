package com.defano.hypercard.search;

import com.defano.hypercard.search.strategy.CharsSearchStrategy;
import com.defano.hypercard.search.strategy.DefaultSearchStrategy;
import com.defano.hypertalk.ast.common.SearchType;

public class SearchFactory {

    public static SearchStrategy searchBy(SearchType type) {
        switch (type) {
            case SUBSTRING:
            case CHARS:
            case STRING:
                return new CharsSearchStrategy();
            case WORDS:
            case WHOLE:
            default:
                return new DefaultSearchStrategy();
        }
    }

}
