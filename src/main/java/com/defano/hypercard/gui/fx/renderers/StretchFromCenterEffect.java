package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.image.BufferedImage;

public class StretchFromCenterEffect extends AnimatedVisualEffect {

    @Override
    public BufferedImage render(BufferedImage from, BufferedImage to, float progress) {
        BufferedImage frame = new BufferedImage(from.getWidth(), from.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate scroll distance
        int stretchDistance = (int) (progress * from.getHeight());
        stretchDistance = stretchDistance <= 0 ? 1 : stretchDistance;

        // Draw from image on frame
        g.drawImage(from, 0, 0, null);

        // Stretch the to image onto the canvas
        BufferedImage resized = new BufferedImage(to.getWidth(), stretchDistance, BufferedImage.TYPE_INT_ARGB);
        Graphics2D rg = resized.createGraphics();

        if (!isBlend()) {
            // Remove stretched region from from image
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT));
            g.fillRect(0, to.getHeight() / 2 - stretchDistance / 2, resized.getWidth(), resized.getHeight());
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        }

        rg.drawImage(to, 0, 0, resized.getWidth(), resized.getHeight(), null);
        g.drawImage(resized, 0, to.getHeight() / 2 - resized.getHeight() / 2, null);
        rg.dispose();

        return frame;
    }
}
