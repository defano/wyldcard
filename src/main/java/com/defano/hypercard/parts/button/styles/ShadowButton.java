/*
 * ShadowButton
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.border.DropShadowBorder;
import com.defano.hypercard.parts.ToolEditablePart;

import java.awt.*;

public class ShadowButton extends AbstractLabelButton {

    private final static int OUTLINE_STROKE = 1;    // Width of button outline
    private final static int SHADOW_STROKE = 2;     // Depth of shadow
    private final static int SHADOW_OFFSET = 5;     // Shadow offset from bottom-left and top-right
    private final static int HILITE_INSET = 1;      // Inset of fill-hilite

    public ShadowButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(new DropShadowBorder(OUTLINE_STROKE, SHADOW_STROKE, SHADOW_OFFSET));
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setPaint(Color.BLACK);
            g.fillRect(
                    getInsets().left + HILITE_INSET,
                    getInsets().top + HILITE_INSET,
                    getWidth() - getInsets().left - getInsets().right - HILITE_INSET * 2,
                    getHeight() - getInsets().top - getInsets().bottom - HILITE_INSET * 2
            );
        }
    }

}
