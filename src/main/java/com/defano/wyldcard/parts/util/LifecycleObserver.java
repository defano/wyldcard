package com.defano.wyldcard.parts.util;

public interface LifecycleObserver {
    /**
     * Invoked to indicate that the object is being started and that it should register any required listeners /
     * observers.
     */
    void onStart();

    /**
     * Invoked to indicate that the object is being stopped and that it should remove itself as a listener of anything
     * that it began observing during a call to {@link #onStart()}.
     *
     * This method is intended to inform the object that it is no longer needed, giving it a chance to un-register
     * listeners (so as not to leak memory or result in overly-burdened observers).
     */
    void onStop();
}
