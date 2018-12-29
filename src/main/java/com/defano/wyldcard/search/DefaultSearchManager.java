package com.defano.wyldcard.search;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.DefaultWyldCardProperties;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.inject.Singleton;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class DefaultSearchManager implements SearchManager {

    private SearchQuery lastQuery;
    private List<SearchResult> results = new ArrayList<>();
    private int nextResult = 0;

    @Override
    public void find(ExecutionContext context, SearchQuery query) throws HtException {

        // Wrap search results
        if (nextResult >= results.size()) {
            nextResult = 0;
        }

        // Continue last query
        if (isResumingSearch(query)) {
            processSearchResult(context, results.get(nextResult++));
        }

        // Start new query
        else {
            lastQuery = query;
            results = SearchIndexer.indexResults(context, query);
            nextResult = 0;

            if (results.isEmpty()) {
                processSearchResult(context, null);
            } else {
                processSearchResult(context, results.get(nextResult++));
            }
        }
    }

    @Override
    public void reset() {
        clearSearchHighlights(new ExecutionContext());
        results.clear();

        WyldCard.getInstance().getWyldCardProperties().defineProperty(DefaultWyldCardProperties.PROP_FOUNDTEXT, new Value(), true);
        WyldCard.getInstance().getWyldCardProperties().defineProperty(DefaultWyldCardProperties.PROP_FOUNDFIELD, new Value(), true);
        WyldCard.getInstance().getWyldCardProperties().defineProperty(DefaultWyldCardProperties.PROP_FOUNDLINE, new Value(), true);
        WyldCard.getInstance().getWyldCardProperties().defineProperty(DefaultWyldCardProperties.PROP_FOUNDCHUNK, new Value(), true);
    }

    private void processSearchResult(ExecutionContext context, SearchResult result) {
        if (result == null) {
            context.setResult(new Value("Not found"));
            Toolkit.getDefaultToolkit().beep();
        } else {
            WyldCard.getInstance().getWyldCardProperties().defineProperty(DefaultWyldCardProperties.PROP_FOUNDTEXT, new Value(result.getFoundText()), true);
            WyldCard.getInstance().getWyldCardProperties().defineProperty(DefaultWyldCardProperties.PROP_FOUNDFIELD, new Value(result.getFoundField(context)), true);
            WyldCard.getInstance().getWyldCardProperties().defineProperty(DefaultWyldCardProperties.PROP_FOUNDLINE, new Value(result.getFoundLine(context)), true);
            WyldCard.getInstance().getWyldCardProperties().defineProperty(DefaultWyldCardProperties.PROP_FOUNDCHUNK, new Value(result.getFoundChunk(context)), true);

            highlightSearchResult(context, result);
        }
    }

    private boolean isResumingSearch(SearchQuery query) {
        return lastQuery != null && lastQuery.equals(query) && results.size() > 0;
    }

}
