package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class BarnDoorOpenEffect extends AnimatedVisualEffect {

    @Override
    public BufferedImage render(BufferedImage from, BufferedImage to, float progress) {
        BufferedImage frame = new BufferedImage(from.getWidth(), from.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate width of the door opening
        int opening = (int) (from.getWidth() * progress);
        opening = opening < 1 ? 1 : opening;

        // Mask the from image, grabbing the center-most portion the width of the opening
        BufferedImage center = to.getSubimage((to.getWidth() / 2) - (opening / 2), 0, opening, to.getHeight());
        g.drawImage(center, (to.getWidth() / 2) - (opening / 2), 0, null);

        // Subdivide the left and right sides to the to image
        BufferedImage leftSide = from.getSubimage(0,0, from.getWidth() / 2, from.getHeight());
        BufferedImage rightSide = from.getSubimage(from.getWidth() / 2, 0, from.getWidth() / 2, from.getHeight());

        // Translate and draw the left "door"
        AffineTransform leftTranslate = new AffineTransform();
        leftTranslate.translate(-(opening / 2), 0);
        g.setTransform(leftTranslate);
        g.drawImage(leftSide, 0, 0, null);

        // Translate and draw the right "door"
        AffineTransform rightTranslate = new AffineTransform();
        rightTranslate.translate(opening / 2, 0);
        g.setTransform(rightTranslate);
        g.drawImage(rightSide, from.getWidth() / 2, 0, null);

        return frame;
    }
}
