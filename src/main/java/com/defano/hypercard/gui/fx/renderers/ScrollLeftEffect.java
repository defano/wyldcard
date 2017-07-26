package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedSegue;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ScrollLeftEffect extends AnimatedSegue {

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {
        BufferedImage frame = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate the scroll distance, in pixels
        int scrollDistance = (int) (progress * src.getWidth());

        // Slide the from image up
        AffineTransform fromTranslate = new AffineTransform();
        fromTranslate.translate(-scrollDistance, 0);
        g.setTransform(fromTranslate);
        g.drawImage(src, 0, 0, null);

        // Slide the to image up underneath it
        AffineTransform toTranslate = new AffineTransform();
        toTranslate.translate(dst.getWidth() - scrollDistance, 0);
        g.setTransform(toTranslate);
        g.drawImage(dst, 0, 0, null);

        return frame;
    }
}
