package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedSegue;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class WipeUpEffect extends AnimatedSegue {

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {
        BufferedImage frame = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate wipe distance
        int distance = (int) (progress * src.getHeight());

        if (!isBlend()) {
            // Truncate the from image (it's getting wiped)
            BufferedImage wiped = src.getSubimage(0, 0, src.getWidth(), src.getHeight() - distance);
            g.drawImage(wiped, 0, 0, null);
        } else {
            g.drawImage(src, 0, 0, null);
        }

        // Slide the to image atop the truncated portion of the from image
        AffineTransform toTranslate = new AffineTransform();
        toTranslate.translate(0, dst.getHeight() - distance);
        g.setTransform(toTranslate);
        g.drawImage(dst, 0, 0, null);

        return frame;
    }
}
