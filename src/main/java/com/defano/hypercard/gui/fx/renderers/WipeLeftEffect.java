package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class WipeLeftEffect extends AnimatedVisualEffect {
    @Override
    public BufferedImage render(BufferedImage from, BufferedImage to, float progress) {

        BufferedImage frame = new BufferedImage(from.getWidth(), from.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate wipe distance
        int distance = (int) (progress * from.getWidth());

        if (!isBlend()) {
            // Truncate the from image (it's getting wiped)
            BufferedImage wiped = from.getSubimage(0, 0, from.getWidth() - distance, from.getHeight());
            g.drawImage(wiped, 0, 0, null);
        } else {
            g.drawImage(from, 0, 0, null);
        }

        // Slide the to image atop the truncated portion of the from image
        AffineTransform toTranslate = new AffineTransform();
        toTranslate.translate(to.getWidth() - distance, 0);
        g.setTransform(toTranslate);
        g.drawImage(to, 0, 0, null);

        return frame;
    }
}
