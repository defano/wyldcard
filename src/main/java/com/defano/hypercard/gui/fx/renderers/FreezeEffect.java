package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.image.BufferedImage;

public class FreezeEffect extends AnimatedVisualEffect {

    public FreezeEffect(BufferedImage lockImage) {
        setFrom(lockImage);
        setDurationMs(Integer.MAX_VALUE);
    }

    @Override
    public BufferedImage render(float progress) {
        return getFrom();
    }
}
