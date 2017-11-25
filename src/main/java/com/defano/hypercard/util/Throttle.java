package com.defano.hypercard.util;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Throttles a repeated invocation of a Runnable to assure that it never executes faster than the provided period.
 */
public class Throttle {

    private final int periodMs;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private List<Future> pendingUpdates = new ArrayList<>();

    public Throttle(int periodMs) {
        this.periodMs = periodMs;
    }

    /**
     * Submit a job (i.e., {@link Runnable}) to be throttled and executed (in the future) on the UI thread.
     *
     * If there are previous jobs pending execution, they are canceled and this job is scheduled for execution after
     * the throttle period has expired.
     *
     * @param runnable The job to submit for throttling.
     */
    public void submitOnUiThread(Runnable runnable) {
        // Cancel pending jobs
        for (Future pendingUpdate : pendingUpdates) {
            pendingUpdate.cancel(false);
        }
        pendingUpdates.clear();

        // Schedule job
        pendingUpdates.add(executor.schedule(() -> SwingUtilities.invokeLater(runnable), periodMs, TimeUnit.MILLISECONDS));
    }

}
