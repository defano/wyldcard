package com.defano.hypercard.border;

import javax.swing.border.Border;
import java.awt.*;

/**
 * Draws a round rectangle border on a Swing component.
 */
public class RoundRectBorder implements Border {

    private final static int OUTLINE_STROKE = 1;
    private final int arcSize;

    public RoundRectBorder(int arcSize) {
        this.arcSize = arcSize;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color oldColor = g.getColor();
        g.translate(x, y);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(c.isEnabled() ? Color.BLACK : Color.GRAY);
        ((Graphics2D)g).setStroke(new BasicStroke(OUTLINE_STROKE));
        g.drawRoundRect(OUTLINE_STROKE / 2, OUTLINE_STROKE / 2, width- OUTLINE_STROKE, height- OUTLINE_STROKE, arcSize, arcSize);

        g.setColor(oldColor);
        g.translate(-x, -y);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(OUTLINE_STROKE, OUTLINE_STROKE, OUTLINE_STROKE, OUTLINE_STROKE);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
