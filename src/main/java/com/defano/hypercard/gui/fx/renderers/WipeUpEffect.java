package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class WipeUpEffect extends AnimatedVisualEffect {

    @Override
    public BufferedImage render(BufferedImage from, BufferedImage to, float progress) {
        BufferedImage frame = new BufferedImage(from.getWidth(), from.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate wipe distance
        int distance = (int) (progress * from.getHeight());

        if (!isBlend()) {
            // Truncate the from image (it's getting wiped)
            BufferedImage wiped = from.getSubimage(0, 0, from.getWidth(), from.getHeight() - distance);
            g.drawImage(wiped, 0, 0, null);
        } else {
            g.drawImage(from, 0, 0, null);
        }

        // Slide the to image atop the truncated portion of the from image
        AffineTransform toTranslate = new AffineTransform();
        toTranslate.translate(0, to.getHeight() - distance);
        g.setTransform(toTranslate);
        g.drawImage(to, 0, 0, null);

        return frame;
    }
}
