package com.defano.hypercard.border;

import javax.swing.border.Border;
import java.awt.*;

/**
 * Draws a round-rectangle border with a single-pixel drop shadow on the bottom-right side.
 */
public class RoundRectShadowBorder implements Border {

    private final static int OUTLINE_STROKE = 1;
    private final static int SHADOW_STROKE = 1;

    private final int arcSize;

    public RoundRectShadowBorder(int arcSize) {
        this.arcSize = arcSize;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setPaint(c.isEnabled() ? Color.BLACK : Color.GRAY);
        g2d.setStroke(new BasicStroke(OUTLINE_STROKE));
        g.drawRoundRect(0, 0, width - OUTLINE_STROKE - SHADOW_STROKE, height - OUTLINE_STROKE - SHADOW_STROKE, arcSize, arcSize);

        // Draw horizontal shadow line
        g2d.setStroke(new BasicStroke(SHADOW_STROKE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(arcSize / 2, height - SHADOW_STROKE, width - arcSize / 2, height - SHADOW_STROKE);

        // Draw vertical shadow line
        g2d.setStroke(new BasicStroke(SHADOW_STROKE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(width - SHADOW_STROKE, height - arcSize / 2, width - SHADOW_STROKE, arcSize / 2);

        // Draw corner shadow
        g2d.setStroke(new BasicStroke(SHADOW_STROKE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawArc(width - arcSize - 2, height - arcSize - 2, arcSize + 1, arcSize + 1, 0, -90);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(OUTLINE_STROKE, OUTLINE_STROKE, OUTLINE_STROKE + SHADOW_STROKE, OUTLINE_STROKE + SHADOW_STROKE);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
