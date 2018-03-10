package com.defano.wyldcard.util;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class ThreadUtils {

    public static void assertDispatchThread() {
        assertOrDie(SwingUtilities.isEventDispatchThread(), "Method must be executed on dispatch thread.");
    }

    public static void assertWorkerThread() {
        assertOrDie(!SwingUtilities.isEventDispatchThread(), "Method must be executed on worker thread.");
    }

    public static void invokeAndWaitAsNeeded(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (InterruptedException| InvocationTargetException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void assertOrDie(boolean condition, String errorMessage) {
        if (!condition) {
            new Throwable(errorMessage).printStackTrace();
        }
    }

}
