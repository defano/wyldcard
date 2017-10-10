package com.defano.hypercard.parts.stack;

import com.defano.hypercard.parts.card.CardPart;

import java.awt.*;

public interface StackObservable extends StackObserver {

    /**
     * {@inheritDoc}
     */
    @Override
    default void onStackOpened(StackPart newStack) {
        // Nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void onCardClosed(CardPart oldCard) {
        // Nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void onCardOpened(CardPart newCard) {
        // Nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void onCardDimensionChanged(Dimension newDimension) {
        // Nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void onStackNameChanged(String newName) {
        // Nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void onCardOrderChanged() {
        // Nothing to do
    }
}
