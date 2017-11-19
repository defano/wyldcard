package com.defano.hypercard.search;

import com.defano.hypercard.parts.bkgnd.BackgroundModel;
import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypercard.parts.field.FieldPart;
import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.runtime.context.HyperCardProperties;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.RemotePartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.utils.Range;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SearchContext {

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
     * foundText, foundChunk and foundField HyperCard properties being updated.
     *
     * If the given query is the same as the last query, this method "continues" the previous search finding and
     * highlighting the next matching substring.
     *
     * @param query The query to perform
     * @throws HtException Thrown if the query refers to a bogus field
     */
    public void find(SearchQuery query) throws HtException {

        // Wrap search results
        if (nextResult >= results.size()) {
            nextResult = 0;
        }

        // Continue last query
        if (isResumingSearch(query)) {
            processSearchResult(results.get(nextResult++));
        }

        // Start new query
        else {
            lastQuery = query;
            results = indexResults(query);
            nextResult = 0;

            if (results.isEmpty()) {
                processSearchResult(null);
            } else {
                processSearchResult(results.get(nextResult++));
            }
        }
    }

    /**
     * Reset the search context to its default, no-query state. Removes all search highlights and resets HyperCard
     * properties to their default, empty state.
     */
    public void reset() {
        clearSearchHighlights();
        results.clear();

        HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDTEXT, new Value(), true);
        HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDFIELD, new Value(), true);
        HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDLINE, new Value(), true);
        HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDCHUNK, new Value(), true);
    }

    private void processSearchResult(SearchResult result) {

        if (result == null) {
            Toolkit.getDefaultToolkit().beep();
        } else {
            highlightSearchResult(result);

            HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDTEXT, new Value(result.getFoundText()), true);
            HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDFIELD, new Value(result.getFoundField()), true);
            HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDLINE, new Value(result.getFoundLine()), true);
            HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_FOUNDCHUNK, new Value(result.getFoundChunk()), true);
        }
    }

    private List<SearchResult> indexResults(SearchQuery query) throws HtException {
        List<SearchResult> results = new ArrayList<>();
        StackModel thisStack = ExecutionContext.getContext().getCurrentStack().getStackModel();

        // Indexing a single, user-specified field
        if (query.isSingleFieldSearch()) {
            FieldModel field = (FieldModel) ExecutionContext.getContext().getPart(query.searchField);

            int cardIndex = ExecutionContext.getContext().getCurrentStack().getDisplayedCard().getCardIndexInStack();
            if (query.searchField instanceof RemotePartSpecifier) {
                cardIndex = ExecutionContext.getContext().getCurrentStack().findRemotePartOwner((RemotePartSpecifier) query.searchField).getCardIndexInStack();
            }

            indexField(query, field, cardIndex, results);
        }

        // Indexing all fields on all cards
        else {
            indexCards(query, thisStack.getCurrentCardIndex(), thisStack.getCardCount(), thisStack, results);
            indexCards(query, 0, thisStack.getCurrentCardIndex(), thisStack, results);
        }

        return results;
    }

    private void indexCards(SearchQuery query, int fromIndex, int toIndex, StackModel thisStack, List<SearchResult> results) {
        for (int thisCardIndex = fromIndex; thisCardIndex < toIndex; thisCardIndex++) {
            CardModel thisCard = thisStack.getCardModel(thisCardIndex);
            BackgroundModel thisBackground = thisStack.getBackground(thisCard.getBackgroundId());

            // Skip unmarked cards if required per query
            if (query.searchOnlyMarkedCards && !thisCard.isMarked()) {
                continue;
            }

            for (FieldModel thisCardField : thisCard.getFieldModels()) {
                indexField(query, thisCardField, thisCardIndex, results);
            }

            for (FieldModel thisBkgndField : thisBackground.getFieldModels()) {
                indexField(query, thisBkgndField, thisCardIndex, results);
            }
        }
    }

    private void indexField(SearchQuery query, FieldModel fieldModel, int cardIndex, List<SearchResult> results) {
        int searchFrom = 0;
        Range result;

        String fieldText = fieldModel.getText();
        do {
            result = SearchFactory.searchBy(query.searchType).search(fieldText, query.searchTerm, searchFrom);

            if (result != null) {
                searchFrom = result.end;
                results.add(new SearchResult(fieldText, result, fieldModel.getOwner(), fieldModel.getId(), cardIndex));
            }

        } while (result != null);
    }

    private void clearSearchHighlights() {
        for (FieldPart parts : ExecutionContext.getContext().getCurrentCard().getFields()) {
            parts.getHyperCardTextPane().clearSearchHilights();
        }
    }

    private void highlightSearchResult(SearchResult result) {

        clearSearchHighlights();

        // Nothing found; clear current highlights and reset search context
        if (result == null) {
            reset();
        }

        // Search result is on a different card; go there
        else if (result.getCardIndex() != ExecutionContext.getContext().getCurrentCard().getCardIndexInStack()) {
            ExecutionContext.getContext().getCurrentStack().goCard(result.getCardIndex(), null, true);
        }

        // Box the found text
        try {
            FieldModel foundFieldModel = (FieldModel) ExecutionContext.getContext().getCurrentCard().findPart(result.getLocalPartSpecifier());
            FieldPart foundField = (FieldPart) ExecutionContext.getContext().getCurrentCard().getPart(foundFieldModel);
            foundField.applySearchHilight(result.getRange());
        } catch (Exception e) {
            throw new IllegalStateException("Bug! Search result refers to a bogus part.", e);
        }
    }

    private boolean isResumingSearch(SearchQuery query) {
        return lastQuery != null && lastQuery.equals(query) && results.size() > 0;
    }

}
