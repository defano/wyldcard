package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ScrollRightEffect extends AnimatedVisualEffect {

    @Override
    public BufferedImage render(BufferedImage from, BufferedImage to, float progress) {
        BufferedImage frame = new BufferedImage(from.getWidth(), from.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate the scroll distance, in pixels
        int scrollDistance = (int) (progress * from.getWidth());

        // Slide the from image to the right
        AffineTransform fromTranslate = new AffineTransform();
        fromTranslate.translate(scrollDistance, 0);
        g.setTransform(fromTranslate);
        g.drawImage(from, 0, 0, null);

        // Slide the to image from the left
        AffineTransform toTranslate = new AffineTransform();
        toTranslate.translate(-to.getWidth() + scrollDistance, 0);
        g.setTransform(toTranslate);
        g.drawImage(to, 0, 0, null);

        return frame;
    }

}
