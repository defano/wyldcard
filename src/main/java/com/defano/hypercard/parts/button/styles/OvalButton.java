/*
 * OvalButton
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.border.OvalBorder;
import com.defano.hypercard.parts.ToolEditablePart;

import java.awt.*;

public class OvalButton extends AbstractLabelButton {

    private final static int OUTLINE_STROKE = 1;    // Width of button outline
    private final static int HILITE_INSET = 1;      // Inset of fill hilite

    public OvalButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(new OvalBorder(OUTLINE_STROKE));
        setOpaque(false);
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setPaint(DEFAULT_HILITE_COLOR);
            g.fillOval(
                    getInsets().left + HILITE_INSET,
                    getInsets().top + HILITE_INSET,
                    getWidth() - getInsets().left - getInsets().right - HILITE_INSET * 2,
                    getHeight() - getInsets().top - getInsets().bottom - HILITE_INSET * 2
            );
        }
    }

}
