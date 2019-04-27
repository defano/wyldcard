package com.defano.wyldcard.parts.stack;

import java.awt.*;

/**
 * An observer of changes to the stack model.
 */
public interface StackObserver {
    /**
     * Fired to indicate the given stack has been opened in HyperCard.
     * @param openedStack The newly opened stack.
     */
    default void onStackOpened(StackPart openedStack) {}

    /**
     * Fired to indicate the given stack has been closed.
     * @param closedStack The stack that has been closed
     */
    default void onStackClosed(StackPart closedStack) {}

    /**
     * Fired to indicate the stack's card size has changed.
     * @param newDimension The new dimensions of the stack.
     */
    default void onStackDimensionChanged(Dimension newDimension) {}

    /**
     * Fired to indicate the name of this stack has changed.
     * @param newName The new name of the stack.
     */
    default void onStackNameChanged(String newName) {}

    /**
     * Fired to indicate that the set or order of cards has changed (typically as a result of sorting).
     */
    default void onCardOrderChanged() {}
}
