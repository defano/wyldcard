package com.defano.wyldcard.runtime;

import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackNavigationObserver;

public interface PeriodicMessageManager extends Runnable, StackNavigationObserver {
    void start();

    void addWithin(PartModel part);

    void removeWithin(PartModel part);
}
