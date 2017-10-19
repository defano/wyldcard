package com.defano.hypercard.border;

import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Draws a configurable-width rectangular boarder on Swing components.
 */
public class RectangleBorder extends LineBorder {

    public RectangleBorder(int outlineStroke) {
        super(Color.BLACK, outlineStroke);
    }

}
