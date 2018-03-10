package com.defano.wyldcard.sound;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SoundPlaybackExecutor extends ThreadPoolExecutor {

    private final static SoundPlaybackExecutor instance = new SoundPlaybackExecutor();
    private AtomicInteger activeChannelsCount = new AtomicInteger(0);

    private SoundPlaybackExecutor() {
        super(4, 4, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    public static SoundPlaybackExecutor getInstance() {
        return instance;
    }

    public int getActiveSoundChannelsCount() {
        return activeChannelsCount.get();
    }

    @Override
    public void execute(Runnable command) {
        super.execute(command);
        activeChannelsCount.incrementAndGet();
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        activeChannelsCount.decrementAndGet();
    }
    
}
