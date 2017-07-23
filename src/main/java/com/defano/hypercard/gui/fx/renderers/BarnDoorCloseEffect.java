package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class BarnDoorCloseEffect extends AnimatedVisualEffect {
    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {
        BufferedImage frame = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate width of the door opening
        int opening = src.getWidth() - (int) (src.getWidth() * progress);
        opening = opening < 1 ? 1 : opening;

        // Mask the from image, grabbing the center-most portion the width of the opening
        BufferedImage center = src.getSubimage((src.getWidth() / 2) - (opening / 2), 0, opening, src.getHeight());
        g.drawImage(center, (src.getWidth() / 2) - (opening / 2), 0, null);

        // Subdivide the left and right sides to the to image
        BufferedImage leftSide = dst.getSubimage(0,0, dst.getWidth() / 2, dst.getHeight());
        BufferedImage rightSide = dst.getSubimage(dst.getWidth() / 2, 0, dst.getWidth() / 2, dst.getHeight());

        // Translate and draw the left "door"
        AffineTransform leftTranslate = new AffineTransform();
        leftTranslate.translate(-(opening / 2), 0);
        g.setTransform(leftTranslate);
        g.drawImage(leftSide, 0, 0, null);

        // Translate and draw the right "door"
        AffineTransform rightTranslate = new AffineTransform();
        rightTranslate.translate(opening / 2, 0);
        g.setTransform(rightTranslate);
        g.drawImage(rightSide, src.getWidth() / 2, 0, null);

        return frame;
    }
}
