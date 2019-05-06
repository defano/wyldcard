package com.defano.wyldcard.thread;

import java.util.concurrent.Callable;

/**
 * A {@link Callable} that cannot throw a checked exception.
 *
 * @param <V> The type returned by the Callable
 */
public interface CallableNoException<V> extends Callable<V> {
    V call();
}
