package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ScrollUpEffect extends AnimatedVisualEffect {

    @Override
    public BufferedImage render(BufferedImage from, BufferedImage to, float progress) {
        BufferedImage frame = new BufferedImage(from.getWidth(), from.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate scroll distance, in pixels
        int scrollDistance = (int) (progress * from.getHeight());

        // Slide from image up
        AffineTransform fromTranslate = new AffineTransform();
        fromTranslate.translate(0, -scrollDistance);
        g.setTransform(fromTranslate);
        g.drawImage(from, 0, 0, null);

        // Slide to image up from bottom of screen
        AffineTransform toTranslate = new AffineTransform();
        toTranslate.translate(0, to.getHeight() - scrollDistance);
        g.setTransform(toTranslate);
        g.drawImage(to, 0, 0, null);

        return frame;
    }

}
