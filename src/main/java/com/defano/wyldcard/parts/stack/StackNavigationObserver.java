package com.defano.wyldcard.parts.stack;

import com.defano.wyldcard.parts.card.CardPart;

public interface StackNavigationObserver {

    /**
     * Fired to indicate the given card has closed (i.e., been navigated away from and no longer visible in the
     * stack window).
     * @param oldCard The card that has closed.
     */
    default void onCardClosed(CardPart oldCard) {}

    /**
     * Fired to indicate the given card has opened (i.e., has been navigated to and now visible in the stack window).
     * @param newCard The card has been opened.
     */
    default void onCardOpened(CardPart newCard) {}

}
