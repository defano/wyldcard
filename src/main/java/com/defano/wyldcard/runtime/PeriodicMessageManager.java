package com.defano.wyldcard.runtime;

import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackNavigationObserver;

public interface PeriodicMessageManager extends Runnable, StackNavigationObserver {
    void start();

    /**
     * Specify that the mouse is presently within the bounds of the given part, so that the part receives the periodic
     * 'mouseWithin' message.
     *
     * @param part The part that the mouse is within.
     */
    void addWithin(PartModel part);

    /**
     * Specify that the mouse has left the bounds of the given part, so that it stops receiving 'mouseWithin' messages.
     * Has no effect if the part was not previously added using {@link #addWithin(PartModel)}.
     *
     * @param part The part that the mouse is not within.
     */
    void removeWithin(PartModel part);
}
