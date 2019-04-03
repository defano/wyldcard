package com.defano.wyldcard.thread;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

public class Invoke {

    /**
     * Invokes the given callable on the Swing UI dispatch thread, returning the result of executing the callable on
     * the current thread. Blocks the current thread until the callable has completed executing.
     * <p>
     * Any exception thrown by the callable will be wrapped inside a {@link RuntimeException} and rethrown.
     *
     * @param callable The callable to execute on the dispatch thread
     * @param <V>      The type of object returned by the callable
     * @return The value returned by the callable
     */
    public static <V> V onDispatch(Callable<V> callable) {
        try {
            return onDispatch(callable, Exception.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes the given callable on the Swing UI dispatch thread, returning the result of executing the callable on
     * the current thread. Blocks the current thread until the callable has completed executing.
     *
     * @param callable The callable to execute on the dispatch thread
     * @param <V>      The type of object returned by the callable
     * @return The value returned by the callable
     * @throws E The exception thrown by the callable if execution of the callable throws an exception.
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <V, E extends Exception> V onDispatch(Callable<V> callable, Class<E> exceptionClass) throws E {
        final Object[] value = new Object[1];
        final Exception[] thrown = new Exception[1];

        onDispatch(() -> {
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
     * Invokes a {@link CheckedRunnable} on the Swing UI dispatch thread, blocking until the runnable has completed,
     * throwing any generated exception on the calling thread.
     * <p>
     * If the current thread is the dispatch thread, the runnable is simply executed. If the current thread is not the
     * dispatch thread, then the current thread is blocked until the dispatch thread has completed executing the
     * runnable.
     *
     * @param r The CheckedRunnable to invoke
     * @param exceptionClass The class of exception thrown by the runnable
     * @param <E> A type of exception
     * @throws E The exception thrown by the runnable
     */
    public static <E extends Exception> void onDispatch(CheckedRunnable<E> r, Class<E> exceptionClass) throws E {
        onDispatch((Callable<Void>) () -> {
            r.run();
            return null;
        }, exceptionClass);
    }

    /**
     * Invokes the given runnable on the Swing UI dispatch thread, blocking until the runnable has completed.
     * <p>
     * If the current thread is the dispatch thread, the runnable is simply executed. If the current thread is not the
     * dispatch thread, then the current thread is blocked until the dispatch thread has completed executing the
     * runnable.
     *
     * @param r The runnable to execute on the dispatch thread.
     */
    public static void onDispatch(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (InterruptedException | InvocationTargetException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
