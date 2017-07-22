package com.defano.hypercard.gui.fx;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AnimatedVisualEffect {

    public abstract BufferedImage render(BufferedImage from, BufferedImage to, float progress);

    private final Set<AnimatedEffectObserver> animatedEffectObservers = new HashSet<>();

    private int durationMs = 1000;
    private int fps = 30;

    private ScheduledExecutorService animatorService;
    private long startTime;
    private BufferedImage from;
    private BufferedImage to;
    private boolean blend = false;

    public void start() {
        this.startTime = System.currentTimeMillis();

        try {
            fireFrameRendered(render(from, to, 0f));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (animatorService != null) {
            animatorService.shutdownNow();
        }

        animatorService = Executors.newSingleThreadScheduledExecutor();
        animatorService.scheduleAtFixedRate(() -> {
            try {
                if (getProgress() < 1.0f) {
                    fireFrameRendered(AnimatedVisualEffect.this.render(from, to, getProgress()));
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

    public boolean isBlend() {
        return blend;
    }

    public void setBlend(boolean blend) {
        this.blend = blend;
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
        float progress = ((float)(System.currentTimeMillis() - startTime) / (float) durationMs);
        return progress < 0f ? 0f : progress > 1.0f ? 1.0f : progress;
    }

}
