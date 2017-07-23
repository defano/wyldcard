package com.defano.hypercard.gui.fx.renderers;

import com.defano.hypercard.gui.fx.AnimatedVisualEffect;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CheckerboardEffect extends AnimatedVisualEffect {

    private int checkerboardSize = 8;

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {

        // Render odd squares...
        if (progress < .5) {
            return renderChecks(src, dst, progress * 2, 0);
        }

        // ... then render even squares
        else {
            return renderChecks(src, dst, 1f, (progress - .5f) * 2);
        }
    }

    private BufferedImage renderChecks(BufferedImage src, BufferedImage dst, float oddProgress, float evenProgress) {
        BufferedImage frame = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        int squareSize = src.getHeight() / getCheckerboardSize();

        // Draw the source image on the canvas
        g.drawImage(src, 0, 0, null);

        boolean oddSquare = false;

        // Then, render each square of the dst image
        for (int y = 0; y < src.getHeight(); y += squareSize) {
            for (int x = 0; x < src.getWidth(); x += squareSize) {

                oddSquare = !oddSquare;     // Odd or even square?

                int squareOpening = (int) (squareSize *(oddSquare ? oddProgress : evenProgress));
                if (squareOpening < 1) continue;

                // Special case: right-most square may exceed screen width
                int thisSquareWidth = (x + squareSize) >= dst.getWidth() ? dst.getWidth() - x : squareSize;

                BufferedImage square = dst.getSubimage(x, y, thisSquareWidth, squareOpening);
                Graphics2D lg = square.createGraphics();

                if (!isBlend()) {
                    // Remove square from src
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT));
                    g.fillRect(x, y, thisSquareWidth, squareOpening);
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
                }

                g.drawImage(square, x, y, null);
                lg.dispose();
            }
        }

        g.dispose();
        return frame;
    }

    public int getCheckerboardSize() {
        return checkerboardSize;
    }

    public void setCheckerboardSize(int checkerboardSize) {
        this.checkerboardSize = checkerboardSize;
    }
}
