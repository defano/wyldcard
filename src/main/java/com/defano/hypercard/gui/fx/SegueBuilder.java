package com.defano.hypercard.gui.fx;

import com.defano.hypercard.gui.fx.renderers.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SegueBuilder {

    private final SegueName name;
    private BufferedImage source;
    private BufferedImage destination;
    private Paint sourcePaint;
    private Paint destinationPaint;
    private int maxFps = 30;
    private int durationMs = 1500;
    private boolean blend = false;
    private Set<SegueAnimationObserver> animationObservers = new HashSet<>();
    private Set<SegueCompletionObserver> completionObservers = new HashSet<>();

    private SegueBuilder(SegueName name) {
        this.name = name;
    }

    public static SegueBuilder of (SegueName name) {
        return new SegueBuilder(name);
    }

    public SegueBuilder withSource (BufferedImage src) {
        this.source = src;
        return this;
    }

    public SegueBuilder withDestination(BufferedImage dst) {
        this.destination = dst;
        return this;
    }

    public SegueBuilder withSource(Paint paint) {
        this.sourcePaint = paint;
        return this;
    }

    public SegueBuilder withDestination(Paint paint) {
        this.destinationPaint = paint;
        return this;
    }

    public SegueBuilder withMaxFramesPerSecond(int maxFps) {
        this.maxFps = maxFps;
        return this;
    }

    public SegueBuilder withDurationMs(int durationMs) {
        this.durationMs = durationMs;
        return this;
    }

    public SegueBuilder withDuration(int duration, TimeUnit unit) {
        this.durationMs = (int) unit.toMillis(duration);
        return this;
    }

    public SegueBuilder alphaBlend(boolean alphaBlend) {
        this.blend = alphaBlend;
        return this;
    }

    public SegueBuilder withAnimationObserver(SegueAnimationObserver observer) {
        this.animationObservers.add(observer);
        return this;
    }

    public SegueBuilder withCompletionObserver(SegueCompletionObserver observer) {
        this.completionObservers.add(observer);
        return this;
    }

    public AnimatedSegue build() {
        if (sourcePaint == null && source == null) {
            throw new IllegalArgumentException("Must specify a source before building.");
        }

        if (destinationPaint == null && destination == null) {
            throw new IllegalArgumentException("Must specify a destination before building.");
        }

        if (source == null && destination == null) {
            throw new IllegalArgumentException("Cannot use paint for both source and destination.");
        }

        // Create paint images as needed
        BufferedImage theSource = source == null ? paintImage(destination.getWidth(), destination.getHeight(), sourcePaint) : source;
        BufferedImage theDestination = destination == null ? paintImage(source.getWidth(), source.getHeight(), destinationPaint) : destination;

        // Resize to largest dimensions of not equal
        if (theSource.getWidth() != theDestination.getWidth() || theSource.getHeight() != theDestination.getHeight()) {
            int targetWidth = Math.max(theSource.getWidth(), theDestination.getWidth());
            int targetHeight = Math.max(theDestination.getWidth(), theDestination.getHeight());

            theSource = enlargeImage(targetWidth, targetHeight, theSource);
            theDestination = enlargeImage(targetWidth, targetHeight, theDestination);
        }

        AnimatedSegue effect = getEffect(name);
        effect.setFrom(theSource);
        effect.setTo(theDestination);
        effect.setDurationMs(durationMs);
        effect.setFps(maxFps);
        effect.setBlend(blend);
        effect.addAnimationObservers(animationObservers);
        effect.addCompletionObservers(completionObservers);

        return effect;
    }

    private AnimatedSegue getEffect(SegueName name) {
        switch (name) {
            case DISSOLVE:
                return new DissolveEffect();
            case SCROLL_LEFT:
                return new ScrollLeftEffect();
            case SCROLL_RIGHT:
                return new ScrollRightEffect();
            case SCROLL_UP:
                return new ScrollUpEffect();
            case SCROLL_DOWN:
                return new ScrollDownEffect();
            case BARN_DOOR_OPEN:
                return new BarnDoorOpenEffect();
            case BARN_DOOR_CLOSE:
                return new BarnDoorCloseEffect();
            case WIPE_LEFT:
                return new WipeLeftEffect();
            case WIPE_RIGHT:
                return new WipeRightEffect();
            case WIPE_UP:
                return new WipeUpEffect();
            case WIPE_DOWN:
                return new WipeDownEffect();
            case IRIS_OPEN:
                return new IrisOpenEffect();
            case IRIS_CLOSE:
                return new IrisCloseEffect();
            case ZOOM_IN:
                return new ZoomInEffect();
            case ZOOM_OUT:
                return new ZoomOutEffect();
            case PLAIN:
                return new FreezeEffect();
            case STRETCH_FROM_TOP:
                return new StretchFromTopEffect();
            case STRETCH_FROM_BOTTOM:
                return new StretchFromBottomEffect();
            case STRETCH_FROM_CENTER:
                return new StretchFromCenterEffect();
            case SHRINK_TO_BOTTOM:
                return new ShrinkToBottomEffect();
            case SHRINK_TO_TOP:
                return new ShrinkToTopEffect();
            case SHRING_TO_CENTER:
                return new ShrinkToCenterEffect();
            case VENETIAN_BLINDS:
                return new BlindsEffect();
            case CHECKERBOARD:
                return new CheckerboardEffect();

            default:
                throw new IllegalArgumentException("Bug! Unhandled visual effect: " + name);
        }
    }

    private BufferedImage enlargeImage(int width, int height, BufferedImage image) {
        BufferedImage enlarged = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = enlarged.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return enlarged;
    }

    private BufferedImage paintImage(int width, int height, Paint paint) {
        BufferedImage enlarged = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = enlarged.createGraphics();
        g.setPaint(paint);
        g.fillRect(0, 0, width, height);
        g.dispose();

        return enlarged;
    }
}
