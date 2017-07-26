package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedSegue;

import java.awt.*;
import java.awt.image.BufferedImage;

public class StretchFromBottomEffect extends AnimatedSegue {

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {
        BufferedImage frame = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate scroll distance
        int stretchDistance = (int) (progress * src.getHeight());
        stretchDistance = stretchDistance <= 0 ? 1 : stretchDistance;

        // Draw from image on frame
        g.drawImage(src, 0, 0, null);

        // Stretch the to image onto the canvas
        BufferedImage resized = new BufferedImage(dst.getWidth(), stretchDistance, BufferedImage.TYPE_INT_ARGB);
        Graphics2D rg = resized.createGraphics();

        if (!isBlend()) {
            // Remove stretched region from from image
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT));
            g.fillRect(0, dst.getHeight() - resized.getHeight(), resized.getWidth(), resized.getHeight());
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        }

        rg.drawImage(dst, 0, 0, resized.getWidth(), resized.getHeight(), null);
        g.drawImage(resized, 0, dst.getHeight() - resized.getHeight(), null);
        rg.dispose();

        return frame;
    }
}
