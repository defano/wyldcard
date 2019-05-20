package com.defano.wyldcard.awt;

import javax.swing.text.*;
import java.awt.*;

/**
 *  Implements a simple highlight painter that renders a rectangle around the
 *  area to be highlighted.
 *
 *  By Rob Camick, https://tips4java.wordpress.com/2008/10/28/rectangle-painter/
 */
public class SquigglePainter extends DefaultHighlighter.DefaultHighlightPainter
{
    public SquigglePainter(Color color)
    {
        super( color );
    }

    /**
     * Paints a portion of a highlight.
     *
     * @param  g the graphics context
     * @param  offs0 the starting model offset >= 0
     * @param  offs1 the ending model offset >= offs1
     * @param  bounds the bounding box of the view, which is not
     *           necessarily the region to paint.
     * @param  c the editor
     * @param  view View painting for
     * @return region drawing occured in
     */
    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view)
    {
        Rectangle r = getDrawingArea(offs0, offs1, bounds, view);

        if (r == null) return null;

        //  Do your custom painting

        Color color = getColor();
        g.setColor(color == null ? c.getSelectionColor() : color);

        //  Draw the squiggles

        int squiggle = 2;
        int twoSquiggles = squiggle * 2;
        int y = r.y + r.height - squiggle;

        for (int x = r.x; x <= r.x + r.width - twoSquiggles; x += twoSquiggles)
        {
            g.drawArc(x, y, squiggle, squiggle, 0, 180);
            g.drawArc(x + squiggle, y, squiggle, squiggle, 180, 181);
        }

        // Return the drawing area

        return r;
    }


    private Rectangle getDrawingArea(int offs0, int offs1, Shape bounds, View view)
    {
        // Contained in view, can just use bounds.

        if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset())
        {
            Rectangle alloc;

            if (bounds instanceof Rectangle)
            {
                alloc = (Rectangle)bounds;
            }
            else
            {
                alloc = bounds.getBounds();
            }

            return alloc;
        }
        else
        {
            // Should only render part of View.
            try
            {
                // --- determine locations ---
                Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1,Position.Bias.Backward, bounds);

                return (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
            }
            catch (BadLocationException e)
            {
                // can't render
            }
        }

        // Can't render

        return null;
    }
}