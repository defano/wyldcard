package com.defano.hypercard.gui.fx;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.gui.fx.renderers.FreezeEffect;
import com.defano.hypertalk.ast.common.VisualEffectSpecifier;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class ScreenCurtainManager implements AnimatedEffectObserver {

    private final static ScreenCurtainManager instance = new ScreenCurtainManager();
    private final Set<ScreenCurtainObserver> curtainObservers = new HashSet<>();

    private AnimatedVisualEffect activeEffect;

    private ScreenCurtainManager() {
    }

    public static ScreenCurtainManager getInstance() {
        return instance;
    }

    public void setScreenLocked(boolean locked) {
        if (locked) {
            startEffect(new FreezeEffect(HyperCard.getInstance().getCard().takeScreenshot()));
        } else if (this.activeEffect instanceof FreezeEffect) {
            cancelEffect();
        }
    }

    public void unlockScreenWithEffect(VisualEffectSpecifier effectSpecifier) {
        if (this.activeEffect instanceof FreezeEffect) {
            this.activeEffect.stop();

            if (effectSpecifier != null) {
                BufferedImage from = activeEffect.getFrom();
                BufferedImage to = HyperCard.getInstance().getCard().takeScreenshot();

                startEffect(VisualEffectFactory.create(effectSpecifier, from, to));
            } else {
                cancelEffect();
            }
        }

        else {
            // Screen is not locked; nothing to do
        }
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

    public void addScreenCurtainObserver(ScreenCurtainObserver observer) {
        curtainObservers.add(observer);
    }

    public void removeScreenCurtainObserver(ScreenCurtainObserver observer) {
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
            for (ScreenCurtainObserver thisObserver : curtainObservers) {
                thisObserver.onCurtainUpdated(screenCurtain);
            }
        });
    }
}
