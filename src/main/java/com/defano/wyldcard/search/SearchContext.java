package com.defano.wyldcard.search;

import com.defano.wyldcard.runtime.HyperCardProperties;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SearchContext implements SearchResultHilighter, SearchIndexer {

    private final static SearchContext instance = new SearchContext();

    private SearchQuery lastQuery;
    private List<SearchResult> results = new ArrayList<>();
    private int nextResult = 0;

    private SearchContext() {
    }

    public static SearchContext getInstance() {
        return instance;
    }

    /**
     * Performs a search of the given query. Search results in field text are highlighted (boxed) and the foundLine,
     * foundText, foundChunk and foundField HyperCard properties are updated accordingly.
     *
     * If the given query is the same as the last query, this method "continues" the previous search, finding and
     * highlighting the next matching string.
     *
     *
     * @param context The execution context.
     * @param query The query to perform
     * @throws HtException Thrown if the query refers to a bogus field
     */
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
            results = indexResults(context, query);
            nextResult = 0;

            if (results.isEmpty()) {
                processSearchResult(context, null);
            } else {
                processSearchResult(context, results.get(nextResult++));
            }
        }
    }

    /**
     * Reset the search context to its default, no-query state. Removes all search highlights and resets HyperCard
     * properties to their default, empty state.
     */
    public void reset() {
        clearSearchHighlights(new ExecutionContext());
        results.clear();

        HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDTEXT, new Value(), true);
        HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDFIELD, new Value(), true);
        HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDLINE, new Value(), true);
        HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDCHUNK, new Value(), true);
    }

    private void processSearchResult(ExecutionContext context, SearchResult result) {
        if (result == null) {
            context.setResult(new Value("Not found"));
            Toolkit.getDefaultToolkit().beep();
        } else {
            HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDTEXT, new Value(result.getFoundText()), true);
            HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDFIELD, new Value(result.getFoundField(context)), true);
            HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDLINE, new Value(result.getFoundLine(context)), true);
            HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDCHUNK, new Value(result.getFoundChunk(context)), true);

            highlightSearchResult(context, result);
        }
    }

    private boolean isResumingSearch(SearchQuery query) {
        return lastQuery != null && lastQuery.equals(query) && results.size() > 0;
    }

}
