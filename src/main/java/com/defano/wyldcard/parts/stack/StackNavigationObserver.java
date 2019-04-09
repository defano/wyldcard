package com.defano.wyldcard.parts.stack;

import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.card.CardPart;

public interface StackNavigationObserver {

    /**
     * Fired to indicate the given card has closed (i.e., been navigated away from and no longer visible in the
     * stack window).
     * @param oldCard The card that has closed.
     * @param newCard
     */
    default void onCardClosed(CardPart oldCard, CardModel newCard) {}

    /**
     * Fired to indicate the given card has opened (i.e., has been navigated to and now visible in the stack window).
     * @param oldCard
     * @param newCard The card has been opened.
     */
    default void onCardOpened(CardModel oldCard, CardPart newCard) {}

}
