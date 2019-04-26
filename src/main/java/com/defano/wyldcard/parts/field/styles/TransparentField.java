package com.defano.wyldcard.parts.field.styles;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.parts.ToolEditablePart;

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
        if (WyldCard.getInstance().getToolsManager().getToolMode() == ToolMode.FIELD) {
            ((Graphics2D) g).setPaint(Color.GRAY);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        super.paint(g);
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
