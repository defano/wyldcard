package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedSegue;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DissolveEffect extends AnimatedSegue {

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {

        BufferedImage frame = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Fade in the to image
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, progress));
        g.drawImage(dst, 0, 0, null);

        // Fade out the from image
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (1.0f - progress)));
        g.drawImage(src, 0, 0, null);

        return frame;
    }
}
