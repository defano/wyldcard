package com.defano.hypercard.util;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class ThreadUtils {

    public static void assertDispatchThread() {
        assert SwingUtilities.isEventDispatchThread();
    }

    public static void invokeAndWaitAsNeeded(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (InterruptedException| InvocationTargetException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
