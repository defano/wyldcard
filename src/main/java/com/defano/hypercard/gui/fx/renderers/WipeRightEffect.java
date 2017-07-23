package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class WipeRightEffect extends AnimatedVisualEffect {

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {
        BufferedImage frame = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate wipe distance
        int distance = (int) (progress * src.getWidth());

        if (!isBlend()) {
            // Truncate the from image (it's getting wiped)
            BufferedImage wiped = src.getSubimage(distance, 0, src.getWidth() - distance, src.getHeight());
            g.drawImage(wiped, distance, 0, null);
        } else {
            g.drawImage(src, 0, 0, null);
        }

        // Slide the to image atop the truncated portion of the from image
        AffineTransform toTranslate = new AffineTransform();
        toTranslate.translate( -dst.getWidth() + distance, 0);
        g.setTransform(toTranslate);
        g.drawImage(dst, 0, 0, null);

        return frame;
    }
}
