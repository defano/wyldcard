package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.image.BufferedImage;

public class FreezeEffect extends AnimatedVisualEffect {

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {

        // No-op effect that simply renders the original image
        return src;
    }
}
