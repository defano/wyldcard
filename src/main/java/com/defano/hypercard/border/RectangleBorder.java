package com.defano.hypercard.border;

import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Draws a configurable-width rectangular boarder on Swing components.
 */
public class RectangleBorder extends LineBorder {

    public RectangleBorder(int outlineStroke) {
        super(Color.BLACK, outlineStroke);
    }

}
