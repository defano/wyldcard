package com.defano.hypercard.gui.fx;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AnimatedSegue {

    public abstract BufferedImage render(BufferedImage src, BufferedImage dst, float progress);

    private final Set<SegueAnimationObserver> animationObserver = new HashSet<>();
    private final Set<SegueCompletionObserver> completionObserver = new HashSet<>();

    private int durationMs = 1000;
    private int fps = 30;

    private ScheduledExecutorService animatorService;
    private long startTime;
    private BufferedImage from;
    private BufferedImage to;
    private boolean blend = false;

    public void start() {
        startTime = System.currentTimeMillis();

        fireFrameRendered(render(from, to, 0f));

        if (animatorService != null) {
            animatorService.shutdownNow();
        }

        animatorService = Executors.newSingleThreadScheduledExecutor();
        animatorService.scheduleAtFixedRate(() -> {
            if (getProgress() < 1.0f) {
                fireFrameRendered(AnimatedSegue.this.render(from, to, getProgress()));
            } else {
                stop();
                fireCompleted();
            }
        }, 0, 1000 / fps, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        animatorService.shutdownNow();
    }

    public void addCompletionObserver(SegueCompletionObserver observer) {
        this.completionObserver.add(observer);
    }

    public void addCompletionObservers(Collection<SegueCompletionObserver> observers) {
        this.completionObserver.addAll(observers);
    }

    public void addAnimationObserver(SegueAnimationObserver observer) {
        this.animationObserver.add(observer);
    }

    public void addAnimationObservers(Collection<SegueAnimationObserver> observers) {
        this.animationObserver.addAll(observers);
    }

    public boolean removeAnimationObserver(SegueAnimationObserver observer) {
        return this.animationObserver.remove(observer);
    }

    public boolean removeCompletionObserver(SegueCompletionObserver observer) {
        return this.completionObserver.remove(observer);
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
        for (SegueAnimationObserver thisObserver : animationObserver) {
            thisObserver.onFrameRendered(image);
        }
    }

    private void fireCompleted() {
        for (SegueCompletionObserver thisObserver : completionObserver) {
            thisObserver.onSegueAnimationCompleted(this);
        }
    }

    private float getProgress() {
        float progress = ((float)(System.currentTimeMillis() - startTime) / (float) durationMs);
        return progress < 0f ? 0f : progress > 1.0f ? 1.0f : progress;
    }

}
