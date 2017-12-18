package com.defano.hypercard.fx;

import java.awt.image.BufferedImage;

/**
 * An observer of changes to the screen curtain.
 */
public interface CurtainObserver {
    void onCurtainUpdated(BufferedImage screenCurtain);
}
