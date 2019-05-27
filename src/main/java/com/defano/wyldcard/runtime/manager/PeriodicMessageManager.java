package com.defano.wyldcard.runtime.manager;

public interface PeriodicMessageManager extends Runnable {
    void start();

    void addIdleObserver(IdleObserver observer);
}
