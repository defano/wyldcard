package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AbstractShrinkEffect extends AnimatedVisualEffect {

    public enum ShrinkDirection {
        TO_TOP, TO_BOTTOM, FROM_CENTER
    }

    private final ShrinkDirection direction;

    public AbstractShrinkEffect(ShrinkDirection direction) {
        this.direction = direction;
    }

    @Override
    public BufferedImage render(BufferedImage from, BufferedImage to, float progress) {
        BufferedImage frame = new BufferedImage(from.getWidth(), from.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate stretch distance
        int stretchDistance = from.getHeight() - (int) (progress * from.getHeight());
        stretchDistance = stretchDistance <= 0 ? 1 : stretchDistance;

        // Draw from image on frame
        g.drawImage(to, 0, 0, null);

        // Shrink the from image off the canvas
        BufferedImage resized = new BufferedImage(from.getWidth(), stretchDistance, BufferedImage.TYPE_INT_ARGB);
        Graphics2D rg = resized.createGraphics();

        rg.drawImage(from, 0, 0, resized.getWidth(), resized.getHeight(), null);
        if (!isBlend()) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        }

        switch (direction) {
            case TO_TOP:
                g.drawImage(resized, 0, 0, null);
                break;
            case TO_BOTTOM:
                g.drawImage(resized, 0, (int)(progress * from.getHeight()), null);
                break;
            case FROM_CENTER:
                g.drawImage(resized, 0, from.getHeight() / 2 - resized.getHeight() / 2, null);
                break;
        }

        rg.dispose();
        g.dispose();

        return frame;
    }

}
