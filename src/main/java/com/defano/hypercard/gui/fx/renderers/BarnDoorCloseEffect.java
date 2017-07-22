package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class BarnDoorCloseEffect extends AnimatedVisualEffect {
    @Override
    public BufferedImage render(BufferedImage from, BufferedImage to, float progress) {
        BufferedImage frame = new BufferedImage(from.getWidth(), from.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate width of the door opening
        int opening = from.getWidth() - (int) (from.getWidth() * progress);
        opening = opening < 1 ? 1 : opening;

        // Mask the from image, grabbing the center-most portion the width of the opening
        BufferedImage center = from.getSubimage((from.getWidth() / 2) - (opening / 2), 0, opening, from.getHeight());
        g.drawImage(center, (from.getWidth() / 2) - (opening / 2), 0, null);

        // Subdivide the left and right sides to the to image
        BufferedImage leftSide = to.getSubimage(0,0, to.getWidth() / 2, to.getHeight());
        BufferedImage rightSide = to.getSubimage(to.getWidth() / 2, 0, to.getWidth() / 2, to.getHeight());

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
