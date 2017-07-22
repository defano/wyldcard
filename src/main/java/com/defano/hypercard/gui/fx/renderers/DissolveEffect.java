package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DissolveEffect extends AnimatedVisualEffect {

    @Override
    public BufferedImage render(BufferedImage from, BufferedImage to, float progress) {

        BufferedImage frame = new BufferedImage(from.getWidth(), from.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Fade in the to image
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, progress));
        g.drawImage(to, 0, 0, null);

        // Fade out the from image
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (1.0f - progress)));
        g.drawImage(from, 0, 0, null);

        return frame;
    }
}
