package com.defano.wyldcard.thread;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Invoke {

    private final static ExecutorService delegatedActionExecutor = Executors.newSingleThreadExecutor();

    /**
     * Synchronously invokes the given callable on the Swing UI dispatch thread, returning the result of executing the
     * callable. Blocks the current thread until the callable has completed executing. Equivalent to simply
     * invoking the callable when executed on the dispatch thread.
     * <p>
     * Any exception thrown by the callable will be wrapped inside a {@link RuntimeException} and rethrown.
     *
     * @param callable The callable to execute on the dispatch thread
     * @param <V>      The type of object returned by the callable
     * @return The value returned by the callable
     */
    public static <V> V onDispatch(CallableNoException<V> callable) {
        if (SwingUtilities.isEventDispatchThread()) {
            return callable.call();
        } else {
            try {
                return onDispatch(callable, Exception.class);
            } catch (Exception e) {
                throw new IllegalStateException("Bug! Should not be possible.");
            }
        }
    }

    /**
     * Executes the given callable using {@link #onDispatch(CallableNoException)}, then waits for the AWT
     * dispatch queue to drain.
     * <p>
     * This method is useful for specialized situations where no further processing on a thread should occur until the
     * given runnable has completed, AND any AWT-related side effects produced executing the callable have also been
     * processed. This is a very expensive operation and should be used only where needed.
     *
     * @param callable The callable to execute on the dispatch thread.
     * @param <V>      The type of object returned by the callable
     * @return The value returned by the callable.
     */
    public static <V> V onDispatchAndFlush(CallableNoException<V> callable) {
        V v = onDispatch(callable);
        flushDispatchQueue();
        return v;
    }

    /**
     * Synchronously invokes the given callable on the Swing UI dispatch thread, returning the result of executing the
     * callable on the current thread. Blocks the current thread until the callable has completed executing.
     *
     * @param callable       The callable to execute on the dispatch thread
     * @param exceptionClass The type of exception that may be thrown by the callable.
     * @param <V>            The type of object returned by the callable
     * @return The value returned by the callable
     * @throws E The exception thrown by the callable if execution of the callable throws an exception.
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <V, E extends Exception> V onDispatch(Callable<V> callable, Class<E> exceptionClass) throws E {

        if (SwingUtilities.isEventDispatchThread()) {
            try {
                return callable.call();
            } catch (Exception e) {
                throw (E) e;
            }
        } else {
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
    }

    /**
     * Executes the given callable using {@link #onDispatch(Callable, Class)}, then waits for the AWT dispatch queue to
     * drain.
     * <p>
     * This method is useful for specialized situations where no further processing on a thread should occur until the
     * given runnable has completed, AND any AWT-related side effects produced executing the callable have also been
     * processed. This is a very expensive operation and should be used only where needed.
     *
     * @param callable       The callable to execute on the dispatch thread
     * @param exceptionClass The type of exception that may be thrown by the callable.
     * @param <E>            A type of exception.
     * @param <V>            The type of object returned by the callable
     * @return The value returned by the callable
     * @throws E The exception thrown by the callable if execution of the callable throws an exception.
     */
    public static <V, E extends Exception> V onDispatchAndFlush(Callable<V> callable, Class<E> exceptionClass) throws E {
        V v = onDispatch(callable, exceptionClass);
        flushDispatchQueue();
        return v;
    }

    /**
     * Invokes a {@link CheckedRunnable} on the Swing UI dispatch thread, throwing any exception generated by the
     * runnable, and blocking until the runnable has completed.
     * <p>
     * If the current thread is the dispatch thread, the runnable is simply executed and no assurances are made that
     * pending UI events are completed.
     *
     * @param r              The CheckedRunnable to invoke
     * @param exceptionClass The class of exception thrown by the runnable
     * @param <E>            A type of exception
     * @throws E The exception thrown by the runnable
     */
    public static <E extends Exception> void onDispatch(CheckedRunnable<E> r, Class<E> exceptionClass) throws E {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            onDispatch((Callable<Void>) () -> {
                r.run();
                return null;
            }, exceptionClass);
        }
    }

    /**
     * Invokes the given runnable on the Swing UI dispatch thread, blocking until the runnable has completed and any
     * enqueued events in the dispatch queue have been processed.
     * <p>
     * If the current thread is the dispatch thread, the runnable is simply executed and no assurances are made that
     * pending UI events are completed.
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
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Flushes the AWT event dispatch queue, blocking the current thread until all events have been processed. Has no
     * effect if the current thread is the event dispatch thread.
     */
    @SuppressWarnings("WeakerAccess")
    public static void flushDispatchQueue() {
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(() -> {
                });
            } catch (InterruptedException | InvocationTargetException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Asynchronously invokes the given runnable on the dispatch thread, after all other pending UI events have been
     * processed.
     *
     * @param r The runnable to execute on the dispatch thread.
     */
    public static void asynchronouslyOnDispatch(Runnable r) {
        SwingUtilities.invokeLater(r);
    }

    /**
     * Asynchronously invokes the given runnable on a worker thread (neither a script executor thread or the Swing
     * dispatch thread).
     *
     * @param r The runnable to execute on the worker thread.
     * @return A Future representing the execution state of the runnable.
     */
    @SuppressWarnings("UnusedReturnValue")
    public static Future<?> asynchronouslyOnWorkerThread(Runnable r) {
        return delegatedActionExecutor.submit(r);
    }

}
