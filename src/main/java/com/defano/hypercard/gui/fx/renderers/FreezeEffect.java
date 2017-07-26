package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedSegue;

import java.awt.image.BufferedImage;

public class FreezeEffect extends AnimatedSegue {

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {

        // No-op effect that simply renders the original image
        return src;
    }
}
