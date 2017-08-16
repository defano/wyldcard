/*
 * RectangularButton
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.gui.border.RectangleBorder;
import com.defano.hypercard.parts.ToolEditablePart;

import java.awt.*;

public class RectangularButton extends AbstractLabelButton {

    private final static int OUTLINE_STROKE = 2;

    public RectangularButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(new RectangleBorder(OUTLINE_STROKE));
        setOpaque(false);
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setPaint(Color.BLACK);
            g.fillRect(OUTLINE_STROKE / 2 + OUTLINE_STROKE, OUTLINE_STROKE / 2 + OUTLINE_STROKE,getWidth() - OUTLINE_STROKE * 2 - OUTLINE_STROKE, getHeight() - OUTLINE_STROKE * 2 - OUTLINE_STROKE);
        }
    }

}
