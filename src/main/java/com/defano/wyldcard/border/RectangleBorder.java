package com.defano.wyldcard.border;

import javax.swing.border.Border;
import java.awt.*;

/**
 * Draws a round rectangle border on a Swing component.
 */
public class RectangleBorder implements Border, ColorStateBorder {

    private final int stokeWidth;

    RectangleBorder(int strokeWidth) {
        this.stokeWidth = strokeWidth;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        Color oldColor = g2d.getColor();
        g2d.translate(x, y);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setPaint(getBorderColor(c));
        g2d.setStroke(new BasicStroke(stokeWidth));
        g2d.drawRect(0, 0, width - stokeWidth, height - stokeWidth);

        g2d.setColor(oldColor);
        g2d.translate(-x, -y);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int floor = (int) Math.floor(stokeWidth / 2.0);
        int ceil = (int) Math.ceil(stokeWidth / 2.0);
        return new Insets(floor, floor, ceil, ceil);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
