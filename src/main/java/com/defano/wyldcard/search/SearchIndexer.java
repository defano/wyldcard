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

public class SearchIndexer {

    /**
     * Executes a {@link SearchQuery} against a single, specified card in the current stack. Has no effect (returns an
     * empty list) if the specified card is eligible for searching (i.e., marked 'dontSearch')
     *
     * @param context The execution context
     * @param query The search query
     * @param cardIndex The zero-based position of the card to search.
     * @return A list of zero or more search results (card, field and offsets of each "hit")
     */
    public static List<SearchResult> indexResults(ExecutionContext context, SearchQuery query, int cardIndex) {
        ArrayList<SearchResult> results = new ArrayList<>();

        StackModel currentStack = context.getCurrentStack().getStackModel();
        CardModel card = currentStack.getCardModel(cardIndex);

        indexCard(context, query, currentStack, card, cardIndex, results);

        return results;
    }

    /**
     * Executes a {@link SearchQuery} against qualifying cards in the current stack.
     *
     * @param context The execution context
     * @param query The search query
     * @return A list of zero or more search results (card, field and offsets of each "hit")
     * @throws HtException Thrown if an error occurs executing the query
     */
    public static List<SearchResult> indexResults(ExecutionContext context, SearchQuery query) throws HtException {
        List<SearchResult> results = new ArrayList<>();
        StackModel thisStack = context.getCurrentStack().getStackModel();

        // Indexing a single, user-specified field
        if (query.isSingleFieldSearch()) {
            PartModel part = context.getPart(query.getSearchField());
            if (!(part instanceof FieldModel)) {
                throw new HtSemanticException("Can't search that.");
            }

            FieldModel field = (FieldModel) part;
            CardModel card = context.getCurrentStack().getDisplayedCard().getPartModel();

            int cardIndex = card.getCardIndexInStack();
            if (query.getSearchField() instanceof CompositePartSpecifier) {
                card = context.getCurrentStack().getStackModel().findOwningCard(context, (CompositePartSpecifier) query.getSearchField());
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

    /**
     * Executes a given {@link SearchQuery} against a range of cards in the current stack.
     *
     * @param context The execution context
     * @param query The search query
     * @param fromIndex The index of the first card in the stack to be indexed (inclusive)
     * @param toIndex The index of the last card in the stack to be indexed (exclusive)
     * @param thisStack The stack whose cards should be searched
     * @param results A mutable list of search results; each hit in the indexed field will be appended to this list
     */
    private static void indexCards(ExecutionContext context, SearchQuery query, int fromIndex, int toIndex, StackModel thisStack, List<SearchResult> results) {
        for (int thisCardIndex = fromIndex; thisCardIndex < toIndex; thisCardIndex++) {
            CardModel thisCard = thisStack.getCardModel(thisCardIndex);

            indexCard(context, query, thisStack, thisCard, thisCardIndex, results);
        }
    }

    /**
     * Executes a given {@link SearchQuery} against all the fields on a given card. Has no effect (returns an
     * empty list) if the specified card is eligible for searching (i.e., marked 'dontSearch')
     *
     * @param context The execution context
     * @param query The search query
     * @param thisStack The stack whose card is to be searched
     * @param thisCard The model of the card to be searched
     * @param thisCardIndex The index (zero-based position) of the card in the stack
     * @param results A mutable list of search results; each hit in the indexed field will be appended to this list
     */
    private static void indexCard(ExecutionContext context, SearchQuery query, StackModel thisStack, CardModel thisCard, int thisCardIndex, List<SearchResult> results) {

        // Ignore cards ineligible for search
        if (!isCardSearchable(context, query, thisCard)) {
            return;
        }

        BackgroundModel thisBackground = thisStack.getBackground(thisCard.getBackgroundId());

        for (FieldModel thisCardField : thisCard.getFieldModels()) {
            indexField(context, query, thisCardField, thisCardIndex, results);
        }

        for (FieldModel thisBkgndField : thisBackground.getFieldModels()) {
            indexField(context, query, thisBkgndField, thisCardIndex, results);
        }
    }

    /**
     * Executes a given {@link SearchQuery} against the text present in a given field.
     *
     * @param context The execution context
     * @param query The search query
     * @param fieldModel The field that should be searched
     * @param cardIndex The index of the card (in the current stack) where the search field is found
     * @param results A mutable list of search results; each hit in the indexed field will be appended to this list
     */
    private static void indexField(ExecutionContext context, SearchQuery query, FieldModel fieldModel, int cardIndex, List<SearchResult> results) {

        // Ignore fields marked "don't search"
        if (fieldModel.getKnownProperty(context, FieldModel.PROP_DONTSEARCH).booleanValue()) {
            return;
        }

        int searchFrom = 0;
        Range result;

        int cardId = context.getCurrentStack().getStackModel().getCardModel(cardIndex).getId(context);
        String fieldText = fieldModel.getText(context, cardId);

        do {
            result = SearchFactory.searchBy(query.getSearchType()).search(fieldText, query.getSearchTerm(), searchFrom);

            if (result != null) {
                searchFrom = result.end;
                results.add(new SearchResult(fieldText, result, fieldModel, cardIndex));
            }

        } while (result != null);
    }

    /**
     * Determines if a given card is eligible for searching based on its 'dontSearch' property and whether the search
     * query calls for searching only marked cards.
     *
     * @param context The execution context
     * @param query The search query
     * @param cardModel The card whose search eligibility is being determined
     * @return True if the card should be searched; false otherwise
     */
    private static boolean isCardSearchable(ExecutionContext context, SearchQuery query, CardModel cardModel) {
        return (!query.isSearchOnlyMarkedCards() || cardModel.isMarked(context)) &&
                !cardModel.getKnownProperty(context, CardModel.PROP_DONTSEARCH).booleanValue()
                && !cardModel.getBackgroundModel().getKnownProperty(context, BackgroundModel.PROP_DONTSEARCH).booleanValue();
    }

}
