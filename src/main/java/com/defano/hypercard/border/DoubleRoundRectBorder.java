package com.defano.hypercard.border;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class DoubleRoundRectBorder implements Border {
    private final int innerWidth;
    private final int outerWidth;
    private final int seperation;
    private final int innerArcSize;
    private final int outerArcSize;

    public DoubleRoundRectBorder(int innerWidth, int innerArcSize, int seperation, int outerWidth, int outerArcSize) {
        this.innerWidth = innerWidth;
        this.seperation = seperation;
        this.outerWidth = outerWidth;
        this.innerArcSize = innerArcSize;
        this.outerArcSize = outerArcSize;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        Color oldColor = g2d.getColor();
        g2d.translate(x, y);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(c.isEnabled() ? Color.BLACK : Color.GRAY);
        g2d.setStroke(new BasicStroke(outerWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(new RoundRectangle2D.Double(outerWidth / 2.0, outerWidth / 2.0, width - outerWidth, height - outerWidth, outerArcSize, outerArcSize));

        g2d.setStroke(new BasicStroke(innerWidth));
        g2d.draw(new RoundRectangle2D.Double(outerWidth + seperation + innerWidth / 2.0, outerWidth + seperation + innerWidth / 2.0, width - outerWidth * 2 - innerWidth - seperation * 2, height - outerWidth * 2 - innerWidth - seperation * 2, innerArcSize, innerArcSize));

        g2d.setColor(oldColor);
        g2d.translate(-x, -y);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int widthSum = innerWidth + outerWidth + seperation;
        return new Insets(widthSum, widthSum, widthSum, widthSum);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
