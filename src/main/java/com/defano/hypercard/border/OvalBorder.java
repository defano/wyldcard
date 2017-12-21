package com.defano.hypercard.border;

import javax.swing.border.Border;
import java.awt.*;

/**
 * Draws an oval/circular border on a Swing component.
 */
public class OvalBorder implements Border {

    private final int outlineStroke;

    public OvalBorder(int outlineStroke) {
        this.outlineStroke = outlineStroke;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(c.isEnabled() ? Color.BLACK : Color.GRAY);

        ((Graphics2D)g).setStroke(new BasicStroke(outlineStroke));
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawOval(Math.max(outlineStroke / 2, 1), Math.max(outlineStroke / 2, 1), width - Math.max(outlineStroke / 2, 2), height - Math.max(outlineStroke / 2, 2));
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
