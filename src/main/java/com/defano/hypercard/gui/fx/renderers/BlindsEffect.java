package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BlindsEffect extends AnimatedVisualEffect {

    private int louverCount = 12;

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {
        BufferedImage frame = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        int louverHeight = src.getHeight() / getLouverCount();
        int louverOpening = (int) (louverHeight * progress);
        louverOpening = louverOpening < 1 ? 1 : louverOpening;

        // Draw the source image on the canvas
        g.drawImage(src, 0, 0, null);

        // Then, render each louver of the dst image
        for (int y = 0; y < src.getHeight(); y += louverHeight) {
            int thisLouverHeight = (y + louverOpening) >= dst.getHeight() ? dst.getHeight() - y : louverOpening;
            BufferedImage louver = dst.getSubimage(0, y, dst.getWidth(), thisLouverHeight);

            Graphics2D lg = louver.createGraphics();

            if (!isBlend()) {
                // Remove louver opening from src image
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT));
                g.fillRect(0, y, dst.getWidth(), thisLouverHeight);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            }

            g.drawImage(louver, 0, y, null);
            lg.dispose();
        }

        g.dispose();
        return frame;
    }

    public int getLouverCount() {
        return louverCount;
    }

    public void setLouverCount(int louverCount) {
        this.louverCount = louverCount;
    }
}
