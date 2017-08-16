package com.defano.hypercard.gui.border;

import javax.swing.border.Border;
import java.awt.*;

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
        g.drawRect(Math.floorDiv(outlineStroke, 2), Math.floorDiv(outlineStroke, 2), width - shadowStroke - outlineStroke, height - shadowStroke - outlineStroke);

        g2d.setStroke(new BasicStroke(shadowStroke));

        // Draw horizontal shadow line
        g2d.drawLine(shadowOffset, height - Math.floorDiv(shadowStroke, 2) - 1, width - shadowStroke, height - Math.floorDiv(shadowStroke, 2) - 1);

        // Draw vertical shadow line
        g2d.drawLine(width - Math.floorDiv(shadowStroke, 2) - 1, height - Math.floorDiv(shadowStroke, 2) - 1, width - Math.floorDiv(shadowStroke, 2) - 1, shadowOffset);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(outlineStroke, outlineStroke, outlineStroke + shadowStroke, outlineStroke + shadowStroke);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
