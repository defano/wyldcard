package com.defano.wyldcard.runtime.manager;

/**
 * An observer of script execution idle events.
 */
public interface IdleObserver {

    /**
     * Invoked to indicate that the HyperTalk script executor has completed running all pending scripts.
     */
    void onIdle();
}
