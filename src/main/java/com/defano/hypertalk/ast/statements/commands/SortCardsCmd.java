package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.hypertalk.ast.model.SortDirection;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.comparator.CardExpressionComparator;
import com.defano.hypertalk.ast.model.SortStyle;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class SortCardsCmd extends Command {

    private final boolean markedCards;
    private final SortDirection direction;
    private final SortStyle style;
    private final Expression expression;
    private final Expression background;

    public SortCardsCmd(ParserRuleContext context, boolean markedCards, SortDirection direction, SortStyle style, Expression expression) {
        this(context, markedCards, null, direction, style, expression);
    }

    public SortCardsCmd(ParserRuleContext context, boolean markedCards, Expression background, SortDirection direction, SortStyle style, Expression expression) {
        super(context, "sort");

        this.markedCards = markedCards;
        this.direction = direction;
        this.style = style;
        this.expression = expression;
        this.background = background;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        // Remember which card we're currently viewing
        int thisCardId = context.getCurrentStack().getDisplayedCard().getPartModel().getId(context);

        // Get a copy of the list of cards in the stack
        List<CardModel> allCards = context.getCurrentStack().getStackModel().getCardModels();

        // Filter list for cards indicated for sorting (i.e., marked cards; cards of a given background)
        List<CardModel> sortCards = filterCards(context, allCards);

        // Sort the indicated cards
        try {
            sortCards.sort(new CardExpressionComparator(context, expression, style, direction));

            // Insert the sorted cards back into the full stack
            List<CardModel> orderedCards = mergeCards(context, allCards, sortCards);

            // Update the stack with the modified card order and invalidate the card cache
            context.getCurrentStack().getStackModel().setCardModels(orderedCards);

        } catch (HtUncheckedSemanticException e) {
            // Error occurred sorting; revert all changes
            context.getCurrentStack().getStackModel().setCardModels(allCards);
            WyldCard.getInstance().showErrorDialogAndAbort(e.getHtCause());
        } finally {
            // Because card order may have changed, lets navigate back to where we started
            int thisCardIdx = indexOfCardId(context, context.getCurrentStack().getStackModel().getCardModels(), thisCardId);
            context.getCurrentStack().invalidateCache(context, thisCardIdx);
        }
    }

    private List<CardModel> filterCards(ExecutionContext context, List<CardModel> cards) throws HtException {
        ArrayList<CardModel> filteredCards = new ArrayList<>();

        for (CardModel thisCard : cards) {
            if (cardMatchesSortCriteria(context, thisCard)) {
                filteredCards.add(thisCard);
            }
        }

        return filteredCards;
    }

    private List<CardModel> mergeCards(ExecutionContext context, List<CardModel> allCards, List<CardModel> filteredCards) throws HtException {
        List<CardModel> merged = new ArrayList<>(allCards);
        int matched = 0;

        for (int index = 0; index < allCards.size(); index++) {
            if (cardMatchesSortCriteria(context, allCards.get(index))) {
                merged.set(index, filteredCards.get(matched++));
            }
        }

        if (matched != filteredCards.size()) {
            throw new ConcurrentModificationException("Stack was modified while sorting.");
        }

        return merged;
    }

    private boolean cardMatchesSortCriteria(ExecutionContext context, CardModel cardModel) throws HtException {
        return cardMatchesBackground(context, cardModel) && cardMatchesMarked(context, cardModel);
    }

    private boolean cardMatchesMarked(ExecutionContext context, CardModel cardModel) {
        return !markedCards || cardModel.get(context, CardModel.PROP_MARKED).booleanValue();
    }

    private boolean cardMatchesBackground(ExecutionContext context, CardModel cardModel) throws HtException {
        if (background == null) {
            return true;
        }

        BackgroundModel backgroundModel = background.partFactor(context, BackgroundModel.class, new HtSemanticException("Can't sort that."));
        for (CardModel thisCard : backgroundModel.getCardModels(context)) {
            if (thisCard.getId(context) == cardModel.getId(context)) {
                return true;
            }
        }

        return false;
    }

    private int indexOfCardId(ExecutionContext context, List<CardModel> cardModels, int id) {
        for (int index = 0; index < cardModels.size(); index++) {
            if (cardModels.get(index).getId(context) == id) {
                return index;
            }
        }

        throw new ConcurrentModificationException("Stack was modified while sorting.");
    }

}
