package com.defano.hypercard.border;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Draws an oval/circular border on a Swing component.
 */
public class OvalBorder implements Border {

    private final int strokeWidth;

    public OvalBorder() {
        this(1);
    }

    public OvalBorder(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        Color oldColor = g2d.getColor();
        g2d.translate(x, y);

        double halfStroke = strokeWidth / 2.0;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(c.isEnabled() ? Color.BLACK : Color.GRAY);
        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.draw(new Ellipse2D.Double(halfStroke, halfStroke, width - strokeWidth, height - strokeWidth));

        g2d.setColor(oldColor);
        g2d.translate(-x, -y);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(strokeWidth, strokeWidth, strokeWidth, strokeWidth);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
