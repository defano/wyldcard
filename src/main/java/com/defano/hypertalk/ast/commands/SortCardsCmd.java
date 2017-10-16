package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.SortDirection;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.comparator.CardExpressionComparator;
import com.defano.hypertalk.comparator.SortStyle;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class SortCardsCmd extends Command {

    private final boolean markedCards;
    private final SortDirection direction;
    private final SortStyle style;
    private final Expression expression;
    private final PartExp background;

    public SortCardsCmd(ParserRuleContext context, boolean markedCards, SortDirection direction, SortStyle style, Expression expression) {
        this(context, markedCards, null, direction, style, expression);
    }

    public SortCardsCmd(ParserRuleContext context, boolean markedCards, PartExp background, SortDirection direction, SortStyle style, Expression expression) {
        super(context, "sort");

        this.markedCards = markedCards;
        this.direction = direction;
        this.style = style;
        this.expression = expression;
        this.background = background;
    }

    @Override
    public void onExecute() throws HtException {
        // Remember which card we're currently viewing
        int thisCardId = HyperCard.getInstance().getDisplayedCard().getCardModel().getId();

        // Get a copy of the list of cards in the stack
        List<CardModel> allCards = HyperCard.getInstance().getStack().getStackModel().getCardModels();

        // Filter list for cards indicated for sorting (i.e., marked cards; cards of a given background)
        List<CardModel> sortCards = filterCards(allCards);

        // Sort the indicated cards
        try {
            StackModel stackModel = HyperCard.getInstance().getStack().getStackModel();
            sortCards.sort(new CardExpressionComparator(stackModel, expression, style, direction));

            // Insert the sorted cards back into the full stack
            List<CardModel> orderedCards = mergeCards(allCards, sortCards);

            // Update the stack with the modified card order and invalidate the card cache
            HyperCard.getInstance().getStack().getStackModel().setCardModels(orderedCards);

        } catch (HtUncheckedSemanticException e) {
            // Error occurred sorting; revert all changes
            HyperCard.getInstance().getStack().getStackModel().setCardModels(allCards);
            HyperCard.getInstance().showErrorDialog(e.getHtCause());
        } finally {
            // Because card order  may have changed, lets navigate back to where we started
            HyperCard.getInstance().getStack().invalidateCache();
            HyperCard.getInstance().getStack().goCard(indexOfCardId(HyperCard.getInstance().getStack().getStackModel().getCardModels(), thisCardId), null, false);
        }
    }

    private List<CardModel> filterCards(List<CardModel> cards) throws HtException {
        ArrayList<CardModel> filteredCards = new ArrayList<>();

        for (CardModel thisCard : cards) {
            if (cardMatchesSortCriteria(thisCard)) {
                filteredCards.add(thisCard);
            }
        }

        return filteredCards;
    }

    private List<CardModel> mergeCards(List<CardModel> allCards, List<CardModel> filteredCards) throws HtException {
        List<CardModel> merged = new ArrayList<>(allCards);
        int matched = 0;

        for (int index = 0; index < allCards.size(); index++) {
            if (cardMatchesSortCriteria(allCards.get(index))) {
                merged.set(index, filteredCards.get(matched++));
            }
        }

        if (matched != filteredCards.size()) {
            throw new ConcurrentModificationException("Stack was modified while sorting.");
        }

        return merged;
    }

    private boolean cardMatchesSortCriteria(CardModel cardModel) throws HtException {
        return cardMatchesBackground(cardModel) && cardMatchesMarked(cardModel);
    }

    private boolean cardMatchesMarked(CardModel cardModel) {
        return !markedCards || cardModel.getKnownProperty(CardModel.PROP_MARKED).booleanValue();
    }

    private boolean cardMatchesBackground(CardModel cardModel) throws HtException {
        if (background == null) {
            return true;
        }

        PartSpecifier specifier = background.evaluateAsSpecifier();

        // Shouldn't be possible, but...
        if (specifier.type() != PartType.BACKGROUND) {
            throw new HtSemanticException("Expression does not refer to a background.");
        }

        int backgroundId = HyperCard.getInstance().getStack().findPart(specifier).getId();

        for (CardModel thisCard : HyperCard.getInstance().getStack().getStackModel().getCardsInBackground(backgroundId)) {
            if (thisCard.getId() == cardModel.getId()) {
                return true;
            }
        }

        return false;
    }

    private int indexOfCardId(List<CardModel> cardModels, int id) {
        for (int index = 0; index < cardModels.size(); index++) {
            if (cardModels.get(index).getId() == id) {
                return index;
            }
        }

        throw new ConcurrentModificationException("Stack was modified while sorting.");
    }

}
