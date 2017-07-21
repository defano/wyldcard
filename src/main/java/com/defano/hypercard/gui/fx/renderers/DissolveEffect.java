package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DissolveEffect extends AnimatedVisualEffect {

    @Override
    public BufferedImage render(float progress) {
        if (from.getWidth() != to.getWidth() || from.getHeight() != to.getHeight()) {
            throw new IllegalStateException("To and from images must be the same size.");
        }

        BufferedImage frame = new BufferedImage(from.getWidth(), from.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, progress / 100f));
        g.drawImage(to, 0, 0, null);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (100.f - progress) / 100f));
        g.drawImage(from, 0, 0, null);

        return frame;
    }
}
