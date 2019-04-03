package com.defano.wyldcard.thread;

import javax.swing.*;

public class ThreadChecker {
    /**
     * Asserts that the current thread is not the Swing dispatch thread; throws a {@link RuntimeException} if this
     * condition is not met.
     */
    public static void assertWorkerThread() {
        assertByThrowing(!SwingUtilities.isEventDispatchThread(), "Method must be executed on a worker thread.");
    }

    private static void assertByThrowing(boolean condition, String errorMessage) {
        if (!condition) {
            throw new RuntimeException(errorMessage);
        }
    }
}
