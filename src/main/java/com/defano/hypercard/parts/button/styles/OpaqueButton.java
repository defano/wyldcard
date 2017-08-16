/*
 * OpaqueButton
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.parts.ToolEditablePart;

import javax.swing.*;
import java.awt.*;

public class OpaqueButton extends AbstractLabelButton {

    public OpaqueButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setPaint(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

}
