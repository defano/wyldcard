package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.runtime.context.ToolsContext;

import javax.swing.*;
import java.awt.*;

public class TransparentField extends HyperCardTextField {

    private final static Color TRANSPARENT = new Color(0, 0, 0, 0);

    public TransparentField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);

        setOpaque(false);
        getTextPane().setOpaque(false);
        getViewport().setOpaque(false);

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

    @Override
    protected void setWideMargins(boolean isWideMargins) {
        if (isWideMargins) {
            setBorder(BorderFactory.createEmptyBorder(WIDE_MARGIN_PX, WIDE_MARGIN_PX, WIDE_MARGIN_PX, WIDE_MARGIN_PX));
        } else {
            setBorder(BorderFactory.createEmptyBorder(NARROW_MARGIN_PX, NARROW_MARGIN_PX, NARROW_MARGIN_PX, NARROW_MARGIN_PX));
        }

        invalidate();
        repaint();
    }
}
