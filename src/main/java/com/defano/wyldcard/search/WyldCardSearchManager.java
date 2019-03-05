package com.defano.wyldcard.search;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.inject.Singleton;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class WyldCardSearchManager implements SearchManager {

    private SearchQuery lastQuery;
    private List<SearchResult> results = new ArrayList<>();
    private int nextResult = 0;

    private Value foundChunk = new Value();
    private Value foundField = new Value();
    private Value foundLine = new Value();
    private Value foundText = new Value();

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

        foundChunk = new Value();
        foundField = new Value();
        foundLine = new Value();
        foundChunk = new Value();
    }

    @Override
    public Value getFoundChunk() {
        return foundChunk;
    }

    @Override
    public Value getFoundField() {
        return foundField;
    }

    @Override
    public Value getFoundLine() {
        return foundLine;
    }

    @Override
    public Value getFoundText() {
        return foundText;
    }

    private void processSearchResult(ExecutionContext context, SearchResult result) {
        if (result == null) {
            context.setResult(new Value("Not found"));
            Toolkit.getDefaultToolkit().beep();
        } else {
            foundText = new Value(result.getFoundText());
            foundField = new Value(result.getFoundField(context));
            foundLine = new Value(result.getFoundLine(context));
            foundChunk = new Value(result.getFoundChunk(context));

            highlightSearchResult(context, result);
        }
    }

    private boolean isResumingSearch(SearchQuery query) {
        return lastQuery != null && lastQuery.equals(query) && results.size() > 0;
    }

}
