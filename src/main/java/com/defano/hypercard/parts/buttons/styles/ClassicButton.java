/*
 * ClassicButton
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.buttons.styles;

import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.buttons.ButtonView;

import java.awt.*;

public class ClassicButton extends AbstractLabelButton implements ButtonView {

    private final static int OUTLINE_SROKE = 2;
    private final static int ARC_SIZE = 10;

    private boolean isHilited = false;

    public ClassicButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
    }

    @Override
    protected void drawBorder(boolean isDisabled, Graphics2D g) {
        g.setPaint(Color.WHITE);
        g.fillRoundRect(OUTLINE_SROKE / 2, OUTLINE_SROKE / 2, getWidth() - OUTLINE_SROKE, getHeight() - OUTLINE_SROKE, ARC_SIZE, ARC_SIZE);

        g.setPaint(textColor(isDisabled));
        g.setStroke(new BasicStroke(OUTLINE_SROKE));
        g.drawRoundRect(OUTLINE_SROKE / 2, OUTLINE_SROKE / 2, getWidth() - OUTLINE_SROKE, getHeight() - OUTLINE_SROKE, ARC_SIZE, ARC_SIZE);

        if (isHilited) {
            g.fillRoundRect(OUTLINE_SROKE / 2 + OUTLINE_SROKE,OUTLINE_SROKE / 2 + OUTLINE_SROKE,getWidth() - OUTLINE_SROKE * 2 - OUTLINE_SROKE, getHeight() - OUTLINE_SROKE * 2 - OUTLINE_SROKE, ARC_SIZE, ARC_SIZE);
        }
    }

    @Override
    protected void setName(boolean isDisabled, String name) {
        setForeground(textColor(isDisabled));
        ClassicButton.super.setText(name);
    }

    @Override
    protected void setHilite(boolean isDisabled, boolean isHilited) {
        if (!isDisabled && !toolEditablePart.isPartToolActive()) {
            this.isHilited = isHilited;
            setForeground(isHilited ? Color.WHITE : textColor(isDisabled));
            repaint();
        }
    }

}
