package com.defano.hypercard.gui.fx;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AnimatedVisualEffect {

    public abstract BufferedImage render(float progress);

    private final Set<AnimatedEffectObserver> animatedEffectObservers = new HashSet<>();

    private int durationMs = 1000;
    private int fps = 20;

    private ScheduledExecutorService animatorService;
    private long startTime;
    protected BufferedImage from;
    protected BufferedImage to;

    public void start() {
        this.startTime = System.currentTimeMillis();

        fireFrameRendered(render(0f));

        if (animatorService != null) {
            animatorService.shutdownNow();
        }

        animatorService = Executors.newSingleThreadScheduledExecutor();
        animatorService.scheduleAtFixedRate(() -> {
            try {
                if (getProgress() < 100) {
                    fireFrameRendered(AnimatedVisualEffect.this.render(getProgress()));
                } else {
                    stop();
                    fireCompleted();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0,1000 / fps, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        animatorService.shutdownNow();
    }

    public void addObserver(AnimatedEffectObserver observer) {
        this.animatedEffectObservers.add(observer);
    }

    public void removeObserver(AnimatedEffectObserver observer) {
        this.animatedEffectObservers.remove(observer);
    }

    public int getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(int durationMs) {
        this.durationMs = durationMs;
    }

    public BufferedImage getFrom() {
        return from;
    }

    public void setFrom(BufferedImage from) {
        this.from = from;
    }

    public BufferedImage getTo() {
        return to;
    }

    public void setTo(BufferedImage to) {
        this.to = to;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    private void fireFrameRendered(BufferedImage image) {
        for (AnimatedEffectObserver thisObserver : animatedEffectObservers) {
            thisObserver.onFrameRendered(image);
        }
    }

    private void fireCompleted() {
        for (AnimatedEffectObserver thisObserver : animatedEffectObservers) {
            thisObserver.onAnimationCompleted(this);
        }
    }

    private float getProgress() {
        float progress = ((float)(System.currentTimeMillis() - startTime) / (float) durationMs) * 100;
        return progress < 0f ? 0f : progress > 100.0f ? 100.0f : progress;
    }

}
