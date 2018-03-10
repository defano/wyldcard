package com.defano.wyldcard.border;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Draws a rectangular border with a drop-shadow on the bottom and right edges of a Swing component.
 */
public class DropShadowBorder implements Border, ColorStateBorder {

    private final int strokeWidth;      // Width of the rectangular border, in px
    private final int shadowWidth;      // Width of the shadow line, in px
    private final int shadowInset;      // Inset of shadow line from bottom-left and top-right, in px

    DropShadowBorder() {
        this(1, 2, 5);
    }

    DropShadowBorder(int strokeWidth, int shadowWidth, int shadowInset) {
        this.strokeWidth = strokeWidth;
        this.shadowWidth = shadowWidth;
        this.shadowInset = shadowInset;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color oldColor = g.getColor();
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(x, y);

        double halfStroke = strokeWidth / 2.0;
        double halfShadow = shadowWidth / 2.0;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Overlay drop-shadow onto border by 1px to prevent any weirdness related to anti-aliasing and HDPI screens;
        // thus shadowWidth needs to be 1px larger than the desired visible shadow.

        g2d.setPaint(getBorderColor(c));
        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.draw(new Rectangle2D.Double(halfStroke, halfStroke, width - shadowWidth - strokeWidth + 1, height - shadowWidth - strokeWidth + 1));

        g2d.setStroke(new BasicStroke(shadowWidth));

        // Draw horizontal shadow line
        g2d.draw(new Line2D.Double(shadowInset, height - halfShadow, width, height - halfShadow));

        // Draw vertical shadow line
        g2d.draw(new Line2D.Double(width - halfShadow, height - halfShadow, width - halfShadow, shadowInset));

        g2d.translate(-x, -y);
        g2d.setColor(oldColor);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(strokeWidth, strokeWidth, shadowWidth + strokeWidth - 1, shadowWidth + strokeWidth - 1);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
