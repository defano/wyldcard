/*
 * TransparentField
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.fields.styles;

import com.defano.hypercard.context.ToolMode;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.parts.ToolEditablePart;

import javax.swing.*;
import java.awt.*;

public class TransparentField extends AbstractTextField {

    private final static Color TRANSPARENT = new Color(0,0,0,0);

    public TransparentField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);

        setOpaque(false);
        textPane.setOpaque(false);
        getViewport().setOpaque(false);

        setBorder(BorderFactory.createEmptyBorder());
        textPane.setBorder(BorderFactory.createEmptyBorder());

        getViewport().setBackground(TRANSPARENT);
        setBackground(TRANSPARENT);
        textPane.setBackground(TRANSPARENT);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (ToolsContext.getInstance().getToolMode() == ToolMode.FIELD) {
            ((Graphics2D) g).setPaint(Color.GRAY);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }
}
