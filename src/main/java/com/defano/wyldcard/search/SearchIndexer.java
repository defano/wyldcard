package com.defano.wyldcard.search;

import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.hypertalk.ast.model.specifiers.CompositePartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.Range;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

public interface SearchIndexer {

    default List<SearchResult> indexResults(ExecutionContext context, SearchQuery query) throws HtException {
        List<SearchResult> results = new ArrayList<>();
        StackModel thisStack = context.getCurrentStack().getStackModel();

        // Indexing a single, user-specified field
        if (query.isSingleFieldSearch()) {
            PartModel part = context.getPart(query.searchField);
            if (!(part instanceof FieldModel)) {
                throw new HtSemanticException("Can't search that.");
            }

            FieldModel field = (FieldModel) part;
            CardModel card = context.getCurrentStack().getDisplayedCard().getCardModel();

            int cardIndex = card.getCardIndexInStack();
            if (query.searchField instanceof CompositePartSpecifier) {
                card = context.getCurrentStack().getStackModel().findOwningCard(context, (CompositePartSpecifier) query.searchField);
                cardIndex = card.getCardIndexInStack();
            }

            if (isCardSearchable(context, query, card)) {
                indexField(context, query, field, cardIndex, results);
            }
        }

        // Indexing all fields on all cards
        else {
            // Index this card to end of the stack...
            indexCards(context, query, thisStack.getCurrentCardIndex(), thisStack.getCardCount(), thisStack, results);

            // ... then index first card up to this card
            indexCards(context, query, 0, thisStack.getCurrentCardIndex(), thisStack, results);
        }

        return results;
    }

    default void indexCards(ExecutionContext context, SearchQuery query, int fromIndex, int toIndex, StackModel thisStack, List<SearchResult> results) {
        for (int thisCardIndex = fromIndex; thisCardIndex < toIndex; thisCardIndex++) {
            CardModel thisCard = thisStack.getCardModel(thisCardIndex);
            BackgroundModel thisBackground = thisStack.getBackground(thisCard.getBackgroundId());

            if (!isCardSearchable(context, query, thisCard)) {
                continue;
            }

            for (FieldModel thisCardField : thisCard.getFieldModels()) {
                indexField(context, query, thisCardField, thisCardIndex, results);
            }

            for (FieldModel thisBkgndField : thisBackground.getFieldModels()) {
                indexField(context, query, thisBkgndField, thisCardIndex, results);
            }
        }
    }

    default void indexField(ExecutionContext context, SearchQuery query, FieldModel fieldModel, int cardIndex, List<SearchResult> results) {
        int searchFrom = 0;
        Range result;

        int cardId = context.getCurrentStack().getStackModel().getCardModel(cardIndex).getId(context);
        String fieldText = fieldModel.getText(context, cardId);

        do {
            result = SearchFactory.searchBy(query.searchType).search(fieldText, query.searchTerm, searchFrom);

            if (result != null) {
                searchFrom = result.end;
                results.add(new SearchResult(fieldText, result, fieldModel.getOwner(), fieldModel.getId(context), cardIndex));
            }

        } while (result != null);
    }

    default boolean isCardSearchable(ExecutionContext context, SearchQuery query, CardModel cardModel) {
        return (!query.searchOnlyMarkedCards || cardModel.isMarked(context)) &&
                !cardModel.getKnownProperty(context, CardModel.PROP_DONTSEARCH).booleanValue()
                && !cardModel.getBackgroundModel().getKnownProperty(context, BackgroundModel.PROP_DONTSEARCH).booleanValue();
    }
}
