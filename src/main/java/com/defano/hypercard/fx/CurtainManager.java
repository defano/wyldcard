package com.defano.hypercard.fx;

import com.defano.hypercard.HyperCard;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.jsegue.AnimatedSegue;
import com.defano.jsegue.SegueAnimationObserver;
import com.defano.jsegue.SegueCompletionObserver;
import com.defano.jsegue.renderers.PlainEffect;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * The "screen curtain" represents a drawable layer that obscures the card with a fixed or animated graphic (for the
 * purposes of 'lock screen' and visual effects). This singleton provides a facade for managing the screen curtain.
 */
public class CurtainManager implements SegueAnimationObserver, SegueCompletionObserver {

    private final static CurtainManager instance = new CurtainManager();
    private final Set<CurtainObserver> curtainObservers = new HashSet<>();

    private CountDownLatch latch = new CountDownLatch(0);
    private AnimatedSegue activeEffect;

    private CurtainManager() {
    }

    public static CurtainManager getInstance() {
        return instance;
    }

    public void setScreenLocked(boolean locked) {
        if (locked && !isScreenLocked()) {
            startEffect(VisualEffectFactory.createScreenLock());
        } else if (this.activeEffect instanceof PlainEffect) {
            cancelEffect();
        }
    }

    public void unlockScreenWithEffect(VisualEffectSpecifier effectSpecifier) {
        if (isScreenLocked()) {
            this.activeEffect.stop();

            // Unlock with effect
            if (effectSpecifier != null) {
                BufferedImage from = activeEffect.getSource();
                BufferedImage to = HyperCard.getInstance().getActiveStackDisplayedCard().getScreenshot();
                startEffect(VisualEffectFactory.create(effectSpecifier, from, to));
            }

            // Just unlock (no effect)
            else {
                cancelEffect();
            }
        }

        // Nothing to do if screen is not locked
    }

    private void startEffect(AnimatedSegue effect) {
        this.latch = new CountDownLatch(1);
        this.activeEffect = effect;
        this.activeEffect.addAnimationObserver(this);
        this.activeEffect.addCompletionObserver(this);
        this.activeEffect.start();
    }

    public void waitForEffectToFinish() {
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void cancelEffect() {
        if (activeEffect != null) {
            activeEffect.stop();
            activeEffect.removeAnimationObserver(this);
            activeEffect.removeCompletionObserver(this);
            latch.countDown();
        }

        activeEffect = null;
        fireOnCurtainUpdated(null);
    }

    public boolean isScreenLocked() {
        return this.activeEffect != null && this.activeEffect instanceof PlainEffect;
    }

    public boolean isEffectActive() {
        return this.activeEffect != null;
    }

    public void addScreenCurtainObserver(CurtainObserver observer) {
        curtainObservers.add(observer);
    }

    public void removeScreenCurtainObserver(CurtainObserver observer) {
        curtainObservers.remove(observer);
    }

    @Override
    public void onSegueAnimationCompleted(AnimatedSegue effect) {
        cancelEffect();
    }

    @Override
    public void onFrameRendered(AnimatedSegue segue, BufferedImage image) {
        fireOnCurtainUpdated(image);
    }

    private void fireOnCurtainUpdated(BufferedImage screenCurtain) {
        SwingUtilities.invokeLater(() -> {
            for (CurtainObserver thisObserver : curtainObservers) {
                thisObserver.onCurtainUpdated(screenCurtain);
            }
        });
    }
}
