package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ScrollDownEffect extends AnimatedVisualEffect {

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {
        BufferedImage frame = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate scroll distance
        int scrollDistance = (int) (progress * src.getHeight());

        // Push the from image down
        AffineTransform fromTranslate = new AffineTransform();
        fromTranslate.translate(0, scrollDistance);
        g.setTransform(fromTranslate);
        g.drawImage(src, 0, 0, null);

        // Push the to image down atop it
        AffineTransform toTranslate = new AffineTransform();
        toTranslate.translate(0, -dst.getHeight() + scrollDistance);
        g.setTransform(toTranslate);
        g.drawImage(dst, 0, 0, null);

        return frame;
    }
}
