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

    private final static int OUTLINE_SROKE = 2;     // Width of button outline
    private final static int HILITE_INSET = 1;      // Inset of hilite fill
    private final static int ARC_DIAMETER = 10;     // Rounded corner diameter

    public ClassicButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(new RoundRectBorder(OUTLINE_SROKE, ARC_DIAMETER));

        // Gotcha! Part model not bound when deserializing (overriding model not desired then, anyway)
        if (toolEditablePart.getPartModel() != null) {
            toolEditablePart.getPartModel().setKnownProperty(ButtonModel.PROP_TEXTFONT, new Value("Chicago"));
            toolEditablePart.getPartModel().setKnownProperty(ButtonModel.PROP_TEXTSIZE, new Value("9"));
        }
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setColor(Color.BLACK);
            g.fillRoundRect(
                    getInsets().left + HILITE_INSET,
                    getInsets().top + HILITE_INSET,
                    getWidth() - getInsets().left - getInsets().right - HILITE_INSET * 2,
                    getHeight() - getInsets().top - getInsets().bottom - HILITE_INSET * 2,
                    ARC_DIAMETER,
                    ARC_DIAMETER
            );
        }
    }

}
