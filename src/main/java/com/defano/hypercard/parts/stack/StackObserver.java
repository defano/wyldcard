/*
 * StackModelObserver
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.stack;

import com.defano.hypercard.parts.card.CardPart;

import java.awt.*;

/**
 * An observer of changes to the stack model.
 */
public interface StackObserver {
    /**
     * Fired to indicate the given stack has been opened in HyperCard.
     * @param newStack The newly opened stack.
     */
    void onStackOpened(StackPart newStack);

    /**
     * Fired to indicate the given card has closed (i.e., been navigated away from and no longer visible in the
     * stack window).
     * @param oldCard The card that has closed.
     */
    void onCardClosed(CardPart oldCard);

    /**
     * Fired to indicate the given card has opened (i.e., has been navigated to and now visible in the stack window).
     * @param newCard The card has been opened.
     */
    void onCardOpened(CardPart newCard);

    /**
     * Fired to indicate the card size has changed and the stack should be resized accordingly.
     * @param newDimension The new dimensions of the stack.
     */
    void onCardDimensionChanged(Dimension newDimension);

    /**
     * Fired to indicate the name of this stack has changed.
     * @param newName The new name of the stack.
     */
    void onStackNameChanged(String newName);

    /**
     * Fired to indicate that the set or order of cards has changed (typically as a result of sorting).
     */
    void onCardOrderChanged();

}
