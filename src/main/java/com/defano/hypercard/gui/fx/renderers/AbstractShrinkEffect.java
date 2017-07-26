package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedSegue;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AbstractShrinkEffect extends AnimatedSegue {

    public enum ShrinkDirection {
        TO_TOP, TO_BOTTOM, FROM_CENTER
    }

    private final ShrinkDirection direction;

    public AbstractShrinkEffect(ShrinkDirection direction) {
        this.direction = direction;
    }

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {
        BufferedImage frame = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate stretch distance
        int stretchDistance = src.getHeight() - (int) (progress * src.getHeight());
        stretchDistance = stretchDistance <= 0 ? 1 : stretchDistance;

        // Draw from image on frame
        g.drawImage(dst, 0, 0, null);

        // Shrink the from image off the canvas
        BufferedImage resized = new BufferedImage(src.getWidth(), stretchDistance, BufferedImage.TYPE_INT_ARGB);
        Graphics2D rg = resized.createGraphics();

        rg.drawImage(src, 0, 0, resized.getWidth(), resized.getHeight(), null);
        if (!isBlend()) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        }

        switch (direction) {
            case TO_TOP:
                g.drawImage(resized, 0, 0, null);
                break;
            case TO_BOTTOM:
                g.drawImage(resized, 0, (int)(progress * src.getHeight()), null);
                break;
            case FROM_CENTER:
                g.drawImage(resized, 0, src.getHeight() / 2 - resized.getHeight() / 2, null);
                break;
        }

        rg.dispose();
        g.dispose();

        return frame;
    }

}
