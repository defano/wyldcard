package com.defano.hypercard.border;

import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Draws a configurable-width rectangular boarder on Swing components.
 */
public class RectangleBorder extends LineBorder {

    private final static int OUTLINE_STROKE = 1;

    public RectangleBorder() {
        super(Color.BLACK, OUTLINE_STROKE);
    }

}
