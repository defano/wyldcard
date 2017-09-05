/*
 * ShadowButton
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.gui.border.DropShadowBorder;
import com.defano.hypercard.parts.ToolEditablePart;

import java.awt.*;

public class ShadowButton extends AbstractLabelButton {

    private final static int OUTLINE_STROKE = 1;
    private final static int SHADOW_STROKE = 2;
    private final static int SHADOW_OFFSET = 5;

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
            g.fillRect(Math.floorDiv(OUTLINE_STROKE, 2) + OUTLINE_STROKE, Math.floorDiv(OUTLINE_STROKE, 2) + OUTLINE_STROKE,getWidth() - OUTLINE_STROKE * 2 - OUTLINE_STROKE - SHADOW_STROKE, getHeight() - OUTLINE_STROKE * 2 - OUTLINE_STROKE - SHADOW_STROKE);
        }
    }

}
