package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.parts.ToolEditablePart;

import javax.swing.*;
import java.awt.*;

public class TransparentField extends AbstractTextField {

    private final static Color TRANSPARENT = new Color(0,0,0,0);

    public TransparentField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);

        setOpaque(false);
        getTextPane().setOpaque(false);
        getViewport().setOpaque(false);

        setBorder(BorderFactory.createEmptyBorder());
        getTextPane().setBorder(BorderFactory.createEmptyBorder());

        getViewport().setBackground(TRANSPARENT);
        setBackground(TRANSPARENT);
        getTextPane().setBackground(TRANSPARENT);
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
