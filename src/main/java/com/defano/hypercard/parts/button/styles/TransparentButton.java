/*
 * TransparentButton
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.gui.border.RectangleBorder;
import com.defano.hypercard.parts.ToolEditablePart;

import javax.swing.*;
import java.awt.*;

public class TransparentButton extends AbstractLabelButton {

    public TransparentButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(BorderFactory.createEmptyBorder());
        setOpaque(false);
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setPaint(DEFAULT_HILITE_COLOR);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            setBorder(BorderFactory.createEmptyBorder());
        } else {
            setBorder(new RectangleBorder(1));
        }
    }
}
