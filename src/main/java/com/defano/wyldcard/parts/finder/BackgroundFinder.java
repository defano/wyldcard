package com.defano.wyldcard.parts.finder;

import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

public interface BackgroundFinder {

    /**
     * Finds the first previous card containing a different background then the current card.
     *
     * @return The first previous card with a different background than the current card.
     * @throws PartException Thrown if no such card can be found.
     */
    default CardModel findPrevBackground(StackModel stackModel) throws PartException {
        int thisCard = stackModel.getCurrentCard().getCardIndexInStack();
        List<CardModel> prevCards = Lists.reverse(stackModel.getCardModels().subList(0, thisCard + 1));

        return findNextBackground(prevCards);
    }

    /**
     * Finds the next card containing a different background then the current card.
     *
     * @return The next card with a different background than the current card.
     * @throws PartException Thrown if no such card can be found.
     */
    default CardModel findNextBackground(StackModel stackModel) throws PartException {
        int thisCard = stackModel.getCurrentCard().getCardIndexInStack();
        int cardCount = stackModel.getCardCount();
        List<CardModel> nextCards = stackModel.getCardModels().subList(thisCard, cardCount);

        return findNextBackground(nextCards);
    }

    /**
     * Finds the next card within a list of card models containing a different background than the first card in this
     * list.
     *
     * @param cardList The list of card models to traverse.
     * @return The first card with a different background than the first card.
     * @throws PartException Thrown if no such card can be found.
     */
    default CardModel findNextBackground(List<CardModel> cardList) throws PartException {
        int thisBackground = cardList.get(0).getBackgroundId();

        Optional<CardModel> nextBkgnd = cardList.stream()
                .filter(cardModel -> cardModel.getBackgroundId() != thisBackground)
                .findFirst();

        if (nextBkgnd.isPresent()) {
            return nextBkgnd.get();
        }

        throw new PartException("No such card.");
    }

}
