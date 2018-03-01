package com.defano.hypercard.border;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Draws a round-rectangle border with a single-pixel drop shadow on the bottom-right side.
 */
public class RoundRectShadowBorder implements Border, ColorStateBorder {

    private final int arcSize;
    private final int strokeWidth;
    private final int shadowWidth;

    RoundRectShadowBorder(int arcSize) {
        this(arcSize, 1, 2);
    }

    RoundRectShadowBorder(int arcSize, int strokeWidth, int shadowWidth) {
        this.arcSize = arcSize;
        this.strokeWidth = strokeWidth;
        this.shadowWidth = shadowWidth;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        Color oldColor = g2d.getColor();
        g2d.translate(x, y);

        double halfStroke = strokeWidth / 2.0;
        double halfShadow = shadowWidth / 2.0;
        double halfArc = arcSize / 2.0;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setPaint(getBorderColor(c));
        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.draw(new RoundRectangle2D.Double(halfStroke, halfStroke, width - strokeWidth - shadowWidth, height - strokeWidth - shadowWidth, arcSize, arcSize));

        // Overlay drop-shadow onto border by 1px to prevent any weirdness related to anti-aliasing and HDPI screens;
        // thus shadowWidth needs to be 1px larger than the desired visible shadow.

        // Draw horizontal shadow line (left-to-right)
        g2d.setStroke(new BasicStroke(shadowWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        g2d.draw(new Line2D.Double(halfArc, height - halfShadow - 1, width - halfArc - shadowWidth, height - halfShadow - 1));

        // Draw vertical shadow line (top-to-bottom)
        g2d.draw(new Line2D.Double(width - halfShadow - 1, halfArc, width - halfShadow - 1, height - halfArc - shadowWidth));

        // Draw corner shadow
        g2d.draw(new Arc2D.Double(width - halfShadow - arcSize - 1, height - halfShadow - arcSize - 1, arcSize, arcSize, 0, -90, shadowWidth > 2 ? Arc2D.CHORD : Arc2D.OPEN));

        g2d.setColor(oldColor);
        g2d.translate(-x, -y);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(strokeWidth, strokeWidth, strokeWidth + shadowWidth, strokeWidth + shadowWidth);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
