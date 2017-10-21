package com.defano.hypercard.parts.field;

import javax.swing.text.*;
import java.awt.*;

public class AutoSelectionHighlighterPainter extends DefaultHighlighter.DefaultHighlightPainter {

    /**
     * Constructs a new highlight painter. If <code>c</code> is null,
     * the JTextComponent will be queried for its selection color.
     */
    public AutoSelectionHighlighterPainter() {
        super(null);        // Paint the hilight in the system hilight color
    }

    /**
     * Paints a portion of a highlight.
     *
     * @param g the graphics context
     * @param offs0 the starting model offset &gt;= 0
     * @param offs1 the ending model offset &gt;= offs1
     * @param bounds the bounding box of the view, which is not
     *        necessarily the region to paint.
     * @param c the editor
     * @param view View painting for
     * @return region drawing occurred in
     */
    @Override
    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
        Color color = getColor();

        if (color == null) {
            g.setColor(c.getSelectionColor());
        }
        else {
            g.setColor(color);
        }

        Rectangle r;

        if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
            // Contained in view, can just use bounds.
            if (bounds instanceof Rectangle) {
                r = (Rectangle) bounds;
            }
            else {
                r = bounds.getBounds();
            }
        }
        else {
            // Should only render part of View.
            try {
                // --- determine locations ---
                Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1,Position.Bias.Backward, bounds);
                r = (shape instanceof Rectangle) ? (Rectangle) shape : shape.getBounds();
            } catch (BadLocationException e) {
                // can't render
                r = null;
            }
        }

        if (r != null) {
            // If we are asked to highlight, we should draw something even
            // if the model-to-view projection is of zero width (6340106).
            r.width = Math.max(c.getWidth() - c.getInsets().right - c.getInsets().left, 1);
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        return r;
    }

}

