package com.defano.hypercard.gui.fx;

import java.awt.image.BufferedImage;

public interface AnimatedEffectObserver {
    void onAnimationCompleted(AnimatedVisualEffect effect);
    void onFrameRendered(BufferedImage image);
}
