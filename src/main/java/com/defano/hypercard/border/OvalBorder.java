package com.defano.hypercard.border;

import javax.swing.border.Border;
import java.awt.*;

public class OvalBorder implements Border {

    private final int outlineStroke;

    public OvalBorder(int outlineStroke) {
        this.outlineStroke = outlineStroke;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(c.isEnabled() ? Color.BLACK : Color.GRAY);
        ((Graphics2D)g).setStroke(new BasicStroke(outlineStroke));
        g.drawOval(outlineStroke / 2, outlineStroke / 2, width - outlineStroke / 2, height - outlineStroke / 2);
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
