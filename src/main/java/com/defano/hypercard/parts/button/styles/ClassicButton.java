/*
 * ClassicButton
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.border.RoundRectBorder;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.button.ButtonComponent;
import com.defano.hypercard.parts.button.ButtonModel;
import com.defano.hypertalk.ast.common.Value;

import java.awt.*;

public class ClassicButton extends AbstractLabelButton implements ButtonComponent {

    private final static int OUTLINE_SROKE = 2;
    private final static int ARC_SIZE = 10;

    public ClassicButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(new RoundRectBorder(OUTLINE_SROKE, ARC_SIZE));

        toolEditablePart.getPart().getPartModel().setKnownProperty(ButtonModel.PROP_TEXTFONT, new Value("Chicago"));
        toolEditablePart.getPart().getPartModel().setKnownProperty(ButtonModel.PROP_TEXTSIZE, new Value("9"));
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setColor(Color.BLACK);
            g.fillRoundRect(OUTLINE_SROKE / 2 + OUTLINE_SROKE,OUTLINE_SROKE / 2 + OUTLINE_SROKE,getWidth() - OUTLINE_SROKE * 2 - OUTLINE_SROKE, getHeight() - OUTLINE_SROKE * 2 - OUTLINE_SROKE, ARC_SIZE, ARC_SIZE);
        }
    }

}
