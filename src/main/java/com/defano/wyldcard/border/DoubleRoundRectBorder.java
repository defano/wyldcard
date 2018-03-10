package com.defano.wyldcard.border;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class DoubleRoundRectBorder implements Border, ColorStateBorder {
    private final int innerWidth;
    private final int outerWidth;
    private final int separation;
    private final int innerArcSize;
    private final int outerArcSize;

    DoubleRoundRectBorder(int innerWidth, int innerArcSize, int separation, int outerWidth, int outerArcSize) {
        this.innerWidth = innerWidth;
        this.separation = separation;
        this.outerWidth = outerWidth;
        this.innerArcSize = innerArcSize;
        this.outerArcSize = outerArcSize;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        Color oldColor = g2d.getColor();
        g2d.translate(x, y);

        double halfOuter = outerWidth / 2.0;
        double halfInner = innerWidth / 2.0;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setPaint(getBorderColor(c));

        // Draw outer stroke
        g2d.setStroke(new BasicStroke(outerWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(new RoundRectangle2D.Double(
                halfOuter,
                halfOuter,
                width - outerWidth,
                height - outerWidth,
                outerArcSize,
                outerArcSize
        ));

        // Draw inner stroke
        g2d.setStroke(new BasicStroke(innerWidth));
        g2d.draw(new RoundRectangle2D.Double(
                outerWidth + separation + innerWidth,
                outerWidth + separation + innerWidth,
                width - outerWidth * 2 - innerWidth * 2 - separation * 2,
                height - outerWidth * 2 - innerWidth * 2 - separation * 2,
                innerArcSize,
                innerArcSize
        ));

        g2d.setColor(oldColor);
        g2d.translate(-x, -y);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int widthSum = innerWidth + outerWidth + separation;
        return new Insets(widthSum, widthSum, widthSum, widthSum);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
