package com.defano.wyldcard.util;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

public class ThreadUtils {

    /**
     * Asserts that the current thread is not the Swing dispatch thread; throws a {@link RuntimeException} if this
     * condition is not met.
     */
    public static void assertWorkerThread() {
        assertByThrowing(!SwingUtilities.isEventDispatchThread(), "Method must be executed on a worker thread.");
    }

    /**
     * Invokes the given callable on the Swing UI dispatch thread, returning the result of executing the callable on
     * the current thread. Blocks the current thread until the callable has completed executing.
     *
     * Any exception thrown by the callable will be wrapped inside a {@link RuntimeException} and rethrown.
     *
     * @param callable The callable to execute on the dispatch thread
     * @param <V> The type of object returned by the callable
     * @return The value returned by the callable
     */
    public static <V> V callAndWaitAsNeeded(Callable<V> callable) {
        try {
            return callCheckedAndWaitAsNeeded(callable, Exception.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes the given callable on the Swing UI dispatch thread, returning the result of executing the callable on
     * the current thread. Blocks the current thread until the callable has completed executing.
     *
     * @param callable The callable to execute on the dispatch thread
     * @param <V> The type of object returned by the callable
     * @return The value returned by the callable
     * @throws Exception The exception thrown by the callable if execution of the callable throws an exception.
     */
    public static <V, E extends Exception> V callCheckedAndWaitAsNeeded(Callable<V> callable, Class<E> exceptionClass) throws E {
        final Object[] value = new Object[1];
        final Exception[] thrown = new Exception[1];

        invokeAndWaitAsNeeded(() -> {
            try {
                value[0] = callable.call();
            } catch (Exception e) {
                thrown[0] = e;
            }
        });

        if (thrown[0] != null) {
            throw (E) thrown[0];
        }

        return (V) value[0];
    }

    /**
     * Invokes the given runnable on the Swing UI dispatch thread, blocking until the runnable has completed. If the
     * current thread is the dispatch thread, the runnable is simply executed. If the current thread is not the dispatch
     * thread, then the current thread is blocked until the dispatch thread has completed executing the runnable.
     *
     * @param r The runnable to execute on the dispatch thread.
     */
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

    private static void assertByThrowing(boolean condition, String errorMessage) {
        if (!condition) {
            throw new RuntimeException(errorMessage);
        }
    }

}
