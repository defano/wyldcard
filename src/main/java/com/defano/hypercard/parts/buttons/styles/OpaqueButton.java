/*
 * OpaqueButton
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.buttons.styles;

import com.defano.hypercard.parts.ToolEditablePart;

import java.awt.*;

public class OpaqueButton extends AbstractLabelButton {

    private boolean isHilited;

    public OpaqueButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
    }

    @Override
    protected void drawBorder(boolean isDisabled, Graphics2D g) {
        g.setPaint(isHilited ? Color.BLACK : Color.WHITE);
        g.fillRect(0,0, getWidth(), getHeight());
    }

    @Override
    protected void setName(boolean isDisabled, String name) {
        setForeground(textColor(isDisabled));
        OpaqueButton.super.setText(name);
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
