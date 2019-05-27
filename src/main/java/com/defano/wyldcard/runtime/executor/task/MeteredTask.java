package com.defano.wyldcard.runtime.executor.task;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A mixin interface used to track the number of executing tasks. This mechanism is used in lieu of
 * {@link ThreadPoolExecutor#getActiveCount()}, which is unreliable.
 */
public interface MeteredTask {
    AtomicInteger runningTasks = new AtomicInteger();

    /**
     * Gets the number of active metered tasks running.
     *
     * @return THe number of running metered tasks.
     */
    static int getRunningTaskCount() {
        return runningTasks.get();
    }

    /**
     * Call to denote that a metered task has begin running.
     */
    default void started() {
        runningTasks.getAndIncrement();
    }

    /**
     * Call to denote that a metered task has stopped running.
     */
    default void stopped() {
        runningTasks.decrementAndGet();
    }
}
