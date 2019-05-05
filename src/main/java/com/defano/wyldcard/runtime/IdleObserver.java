package com.defano.wyldcard.runtime;

public interface IdleObserver {

    /**
     * Invoked to indicate that the HyperTalk script executor has completed running all pending scripts.
     */
    void onIdle();
}
