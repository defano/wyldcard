package com.defano.hypercard.border;

import javax.swing.border.Border;
import java.awt.*;

/**
 * Draws a single-pixel drop-shadow on the bottom and right edges of a Swing component.
 */
public class DropShadowBorder implements Border {

    private final int outlineStroke;
    private final int shadowStroke;
    private final int shadowOffset;

    public DropShadowBorder(int outlineStrokeWidth, int shadowStrokeWidth, int shadowOffset) {
        this.outlineStroke = outlineStrokeWidth;
        this.shadowStroke = shadowStrokeWidth;
        this.shadowOffset = shadowOffset;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(c.isEnabled() ? Color.BLACK : Color.GRAY);
        g2d.setStroke(new BasicStroke(outlineStroke));
        g.drawRect(outlineStroke / 2, outlineStroke / 2, width - shadowStroke - outlineStroke / 2, height - shadowStroke - outlineStroke / 2);

        g2d.setStroke(new BasicStroke(shadowStroke));

        // Draw horizontal shadow line
        g2d.drawLine(shadowOffset, height - Math.floorDiv(shadowStroke, 2), width, height - Math.floorDiv(shadowStroke, 2));

        // Draw vertical shadow line
        g2d.drawLine(width - Math.floorDiv(shadowStroke, 2), height - Math.floorDiv(shadowStroke, 2) - 1, width - Math.floorDiv(shadowStroke, 2), shadowOffset);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(outlineStroke, outlineStroke, shadowStroke + outlineStroke, shadowStroke + outlineStroke);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
