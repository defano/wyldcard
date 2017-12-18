package com.defano.hypercard.border;

import javax.swing.border.Border;
import java.awt.*;

/**
 * Draws a round rectangle border on a Swing component.
 */
public class RoundRectBorder implements Border {

    private final int outlineStroke;
    private final int arcSize;

    public RoundRectBorder(int outlineStroke, int arcSize) {
        this.outlineStroke = outlineStroke;
        this.arcSize = arcSize;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(c.isEnabled() ? Color.BLACK : Color.GRAY);
        ((Graphics2D)g).setStroke(new BasicStroke(outlineStroke));
        g.drawRoundRect(outlineStroke / 2, outlineStroke / 2, width - outlineStroke, height - outlineStroke, arcSize, arcSize);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(outlineStroke, outlineStroke, outlineStroke, outlineStroke);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
