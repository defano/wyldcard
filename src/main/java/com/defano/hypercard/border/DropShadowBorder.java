package com.defano.hypercard.border;

import javax.swing.border.Border;
import java.awt.*;

/**
 * Draws a single-pixel drop-shadow on the bottom and right edges of a Swing component.
 */
public class DropShadowBorder implements Border {

    private final static int OUTLINE_STROKE = 1;
    private final static int SHADOW_STROKE = 2;
    private final static int SHADOW_INSET = 5;

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setPaint(c.isEnabled() ? Color.BLACK : Color.GRAY);
        g2d.setStroke(new BasicStroke(OUTLINE_STROKE));
        g.drawRect(OUTLINE_STROKE / 2, OUTLINE_STROKE / 2, width - SHADOW_STROKE, height - SHADOW_STROKE);

        g2d.setStroke(new BasicStroke(SHADOW_STROKE));

        // Draw horizontal shadow line
        g2d.drawLine(SHADOW_INSET, height - Math.floorDiv(SHADOW_STROKE, 2), width, height - Math.floorDiv(SHADOW_STROKE, 2));

        // Draw vertical shadow line
        g2d.drawLine(width - Math.floorDiv(SHADOW_STROKE, 2), height - Math.floorDiv(SHADOW_STROKE, 2) - 1, width - Math.floorDiv(SHADOW_STROKE, 2), SHADOW_INSET);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(OUTLINE_STROKE, OUTLINE_STROKE, SHADOW_STROKE + OUTLINE_STROKE, SHADOW_STROKE + OUTLINE_STROKE);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
