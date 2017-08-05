package com.defano.hypercard.gui.border;

import javax.swing.border.Border;
import java.awt.*;

public class DropShadowBorder implements Border {

    private final static int OUTLINE_SROKE = 2;
    private final static int SHADOW_STROKE = 2;
    private final static int SHADOW_OFFSET = 5;

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(OUTLINE_SROKE));
        g.drawRect(OUTLINE_SROKE / 2, OUTLINE_SROKE / 2, width - SHADOW_STROKE - OUTLINE_SROKE, height - SHADOW_STROKE - OUTLINE_SROKE);

        g2d.setStroke(new BasicStroke(SHADOW_STROKE));

        // Draw horizontal shadow line
        g2d.drawLine(SHADOW_OFFSET, height - SHADOW_STROKE / 2, width, height - SHADOW_STROKE / 2);

        // Draw vertical shadow line
        g2d.drawLine(width - SHADOW_STROKE / 2, height - SHADOW_STROKE / 2, width - SHADOW_STROKE / 2, SHADOW_OFFSET);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(OUTLINE_SROKE,OUTLINE_SROKE, OUTLINE_SROKE + SHADOW_STROKE, OUTLINE_SROKE + SHADOW_STROKE);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
