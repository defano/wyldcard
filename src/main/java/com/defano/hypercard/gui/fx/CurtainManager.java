package com.defano.hypercard.gui.fx;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.gui.fx.renderers.FreezeEffect;
import com.defano.hypertalk.ast.common.VisualEffectSpecifier;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class CurtainManager implements AnimatedEffectObserver {

    private final static CurtainManager instance = new CurtainManager();
    private final Set<CurtainObserver> curtainObservers = new HashSet<>();

    private AnimatedVisualEffect activeEffect;

    private CurtainManager() {
    }

    public static CurtainManager getInstance() {
        return instance;
    }

    public void setScreenLocked(boolean locked) {
        if (locked && !isScreenLocked()) {
            HyperCard.getInstance().getStack().takeScreenshot();
            startEffect(VisualEffectFactory.createScreenLock());
        } else if (this.activeEffect instanceof FreezeEffect) {
            cancelEffect();
        }
    }

    public void unlockScreenWithEffect(VisualEffectSpecifier effectSpecifier) {
        if (isScreenLocked()) {
            this.activeEffect.stop();

            // Unlock with effect
            if (effectSpecifier != null) {
                BufferedImage from = activeEffect.getFrom();
                BufferedImage to = HyperCard.getInstance().getCard().getScreenshot();
                startEffect(VisualEffectFactory.create(effectSpecifier, from, to));
            }

            // Just unlock (no effect)
            else {
                cancelEffect();
            }
        }

        // Nothing to do if screen is not locked
    }

    private void startEffect(AnimatedVisualEffect effect) {
        this.activeEffect = effect;
        this.activeEffect.addObserver(this);
        this.activeEffect.start();
    }

    public void cancelEffect() {
        if (activeEffect != null) {
            activeEffect.stop();
            activeEffect.removeObserver(this);
        }

        activeEffect = null;
        fireOnCurtainUpdated(null);
    }

    public boolean isScreenLocked() {
        return this.activeEffect != null && this.activeEffect instanceof FreezeEffect;
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
    public void onAnimationCompleted(AnimatedVisualEffect effect) {
        cancelEffect();
    }

    @Override
    public void onFrameRendered(BufferedImage image) {
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
