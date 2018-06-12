package com.defano.wyldcard.fx;

import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.jsegue.AnimatedSegue;
import com.defano.jsegue.SegueAnimationObserver;
import com.defano.jsegue.SegueCompletionObserver;
import com.defano.jsegue.renderers.PlainEffect;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * The "screen curtain" represents a drawable layer that obscures the card with a fixed or animated graphic (for the
 * purposes of 'lock screen' and visual effects). This class provides a facade for managing the screen curtain.
 */
public class CurtainManager implements SegueAnimationObserver, SegueCompletionObserver {

    private final Set<CurtainObserver> curtainObservers = new HashSet<>();
    private CountDownLatch latch = new CountDownLatch(0);
    private AnimatedSegue activeEffect;

    public void setScreenLocked(ExecutionContext context, boolean locked) {

        // Nothing to do when trying to lock a locked screen
        if (locked && isScreenLocked()) {
            return;
        }

        if (locked) {
            startEffect(VisualEffectFactory.createScreenLock(context));
        } else if (this.activeEffect instanceof PlainEffect) {
            cancelEffect();
        }
    }

    public void unlockScreenWithEffect(ExecutionContext context, VisualEffectSpecifier effectSpecifier) {
        if (isScreenLocked()) {
            this.activeEffect.stop();

            // Unlock with effect
            if (effectSpecifier != null) {
                BufferedImage from = activeEffect.getSource();
                BufferedImage to = context.getCurrentStack().getDisplayedCard().getScreenshot();
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

    private void cancelEffect() {
        if (activeEffect != null) {
            activeEffect.stop();
            activeEffect.removeAnimationObserver(this);
            activeEffect.removeCompletionObserver(this);
            latch.countDown();
        }

        activeEffect = null;
        fireOnCurtainUpdated(null);
    }

    private boolean isScreenLocked() {
        return this.activeEffect != null && this.activeEffect instanceof PlainEffect;
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
