package com.defano.wyldcard.part.stack;

import com.defano.wyldcard.part.card.CardModel;
import com.defano.wyldcard.part.card.CardPart;

/**
 * An observer of card-to-card stack navigation events.
 */
public interface StackNavigationObserver {

    /**
     * Indicates that the actively displayed card in a stack has changed (as the result of a navigation action).
     *
     * @param prevCard The previously displayed card that has been removed from the stack window.
     * @param nextCard The new card now displayed in the stack window.
     */
    default void onDisplayedCardChanged(CardModel prevCard, CardPart nextCard) {}
}
