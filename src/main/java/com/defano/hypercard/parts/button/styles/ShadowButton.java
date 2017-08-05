/*
 * ShadowButton
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.parts.ToolEditablePart;

import java.awt.*;

public class ShadowButton extends AbstractLabelButton {

    private final static int OUTLINE_SROKE = 2;
    private final static int SHADOW_STROKE = 2;
    private final static int SHADOW_OFFSET = 5;

    private boolean isHilited = false;

    public ShadowButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
    }

    @Override
    protected void drawBorder(boolean isDisabled, Graphics2D g) {
        g.setPaint(Color.WHITE);
        g.fillRect(OUTLINE_SROKE / 2, OUTLINE_SROKE / 2, getWidth() - OUTLINE_SROKE - SHADOW_STROKE, getHeight() - OUTLINE_SROKE - SHADOW_STROKE);

        g.setPaint(textColor(isDisabled));
        g.setStroke(new BasicStroke(OUTLINE_SROKE));
        g.drawRect(OUTLINE_SROKE / 2, OUTLINE_SROKE / 2, getWidth() - OUTLINE_SROKE - SHADOW_STROKE, getHeight() - OUTLINE_SROKE - SHADOW_STROKE);
        g.drawLine(SHADOW_OFFSET, getHeight() - SHADOW_STROKE / 2, getWidth(), getHeight() - SHADOW_STROKE / 2);
        g.drawLine(getWidth() - SHADOW_STROKE / 2, getHeight() - SHADOW_STROKE / 2, getWidth() - SHADOW_STROKE / 2, SHADOW_OFFSET);

        if (isHilited) {
            g.fillRect(OUTLINE_SROKE / 2 + OUTLINE_SROKE,OUTLINE_SROKE / 2 + OUTLINE_SROKE,getWidth() - OUTLINE_SROKE * 2 - OUTLINE_SROKE - SHADOW_STROKE, getHeight() - OUTLINE_SROKE * 2 - OUTLINE_SROKE - SHADOW_STROKE);
        }
    }

    @Override
    protected void setName(boolean isDisabled, String name) {
        setForeground(textColor(isDisabled));
        ShadowButton.super.setText(name);
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
