package com.defano.wyldcard.patterns;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * A button on the pattern palette.
 * <p>
 * Pattern buttons have unique border and selection requirements:
 * <p>
 * They are intended to be placed immediately adjacent to one another with no margin or space between one another. As
 * such, each button draws only half of its border to prevent a double-wide border where buttons meet. Furthermore,
 * we don't want to draw the left frame of the border for buttons which are adjacent to the left-most edge of the
 * palette window so this class offers the ability to specify which edges are drawn.
 * <p>
 * These "buttons" are implemented as JLabel because certain Look-and-Feels force margin between buttons (Motif is
 * particularly bad about this); labels are a work-around.
 */
public class PatternPaletteButton extends JLabel {

    // Selected/unselected borders
    private final Border selectedBorder = new SelectedBorder();
    private final Border unselectedBorder = new UnselectedBorder();

    private int patternId;          // Pattern to render
    private boolean selected;       // Is pattern selected

    // Which edges of the border to draw
    private boolean leftFrame = true;
    private boolean topFrame = true;

    public PatternPaletteButton() {
        super();

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                invalidatePattern();
            }
        });
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        invalidateSelection();
    }

    public boolean isTopFrame() {
        return topFrame;
    }

    public void setTopFrame(boolean topFrame) {
        this.topFrame = topFrame;
    }

    public boolean isLeftFrame() {
        return leftFrame;
    }

    public void setLeftFrame(boolean leftFrame) {
        this.leftFrame = leftFrame;
    }

    public int getPatternId() {
        return patternId;
    }

    public void setPatternId(int patternId) {
        this.patternId = patternId;
        invalidatePattern();
    }

    private void invalidateSelection() {
        if (selected) {
            setBorder(selectedBorder);
        } else {
            setBorder(unselectedBorder);
        }
    }

    private void invalidatePattern() {
        setIcon(new ImageIcon(createIconForButton(getWidth(), getHeight(), patternId)));
        invalidateSelection();
    }

    private BufferedImage createIconForButton(int width, int height, int patternId) {
        width = Math.max(1, width);
        height = Math.max(1, height);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setPaint(HyperCardPatternFactory.getInstance().getPattern(patternId));
        g.fillRect(0, 0, width, height);

        g.dispose();

        return image;
    }

    /**
     * Border that draws the {@link UnselectedBorder} frame, plus a thicker, inner highlight
     */
    private class SelectedBorder extends UnselectedBorder {

        final static int FRAME_WIDTH = 4;

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;

            float topFrameWidth = topFrame ? UnselectedBorder.FRAME_WIDTH : 0;
            float leftFrameWidth = leftFrame ? UnselectedBorder.FRAME_WIDTH : 0;

            super.paintBorder(c, g, x, y, width, height);

            // Draw inner frame
            g2d.setPaint(SystemColor.textHighlight);
            g2d.setStroke(new BasicStroke(FRAME_WIDTH));
            g2d.draw(new Rectangle2D.Float(FRAME_WIDTH / 2.0f + leftFrameWidth, FRAME_WIDTH / 2.0f + topFrameWidth, width - FRAME_WIDTH - leftFrameWidth, height - FRAME_WIDTH - topFrameWidth));

        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    /**
     * Border that draws a thin frame on the top/left sides of the button
     */
    private class UnselectedBorder implements Border {

        final static int FRAME_WIDTH = 1;

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setPaint(Color.BLACK);
            g2d.setStroke(new BasicStroke(FRAME_WIDTH));

            if (topFrame) {
                g2d.draw(new Line2D.Float(x, y + FRAME_WIDTH / 2.0f, width, y + FRAME_WIDTH / 2.0f));
            }

            if (leftFrame) {
                g2d.draw(new Line2D.Float(x + FRAME_WIDTH / 2.0f, y, FRAME_WIDTH / 2.0f, height));
            }
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    }
}

