package com.defano.wyldcard.search;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.specifiers.CompositePartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.Range;

import java.util.ArrayList;
import java.util.List;

public interface SearchIndexer {

    default List<SearchResult> indexResults(SearchQuery query) throws HtException {
        List<SearchResult> results = new ArrayList<>();
        StackModel thisStack = ExecutionContext.getContext().getCurrentStack().getStackModel();

        // Indexing a single, user-specified field
        if (query.isSingleFieldSearch()) {
            PartModel part = ExecutionContext.getContext().getPart(query.searchField);
            if (!(part instanceof FieldModel)) {
                throw new HtSemanticException("Can't search that.");
            }

            FieldModel field = (FieldModel) part;

            int cardIndex = ExecutionContext.getContext().getCurrentStack().getDisplayedCard().getCardModel().getCardIndexInStack();
            if (query.searchField instanceof CompositePartSpecifier) {
                cardIndex = ExecutionContext.getContext().getCurrentStack().getStackModel().findOwningCard((CompositePartSpecifier) query.searchField).getCardIndexInStack();
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

    default void indexCards(SearchQuery query, int fromIndex, int toIndex, StackModel thisStack, List<SearchResult> results) {
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

    default void indexField(SearchQuery query, FieldModel fieldModel, int cardIndex, List<SearchResult> results) {
        int searchFrom = 0;
        Range result;

        int cardId = WyldCard.getInstance().getActiveStack().getStackModel().getCardModel(cardIndex).getId();
        String fieldText = fieldModel.getText(cardId);

        do {
            result = SearchFactory.searchBy(query.searchType).search(fieldText, query.searchTerm, searchFrom);

            if (result != null) {
                searchFrom = result.end;
                results.add(new SearchResult(fieldText, result, fieldModel.getOwner(), fieldModel.getId(), cardIndex));
            }

        } while (result != null);
    }
}
