package com.defano.wyldcard.runtime.manager;

import com.defano.wyldcard.part.stack.StackNavigationObserver;

public interface PeriodicMessageManager extends Runnable, StackNavigationObserver {
    void start();

    void addIdleObserver(IdleObserver observer);
}
