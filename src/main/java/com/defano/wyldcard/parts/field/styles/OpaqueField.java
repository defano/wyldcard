package com.defano.wyldcard.parts.field.styles;

import com.defano.wyldcard.parts.ToolEditablePart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class OpaqueField extends HyperCardTextField {

    public OpaqueField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    protected void setWideMargins(boolean isWideMargins) {
        if (isWideMargins) {
            setBorder(new EmptyBorder(WIDE_MARGIN_PX, WIDE_MARGIN_PX, WIDE_MARGIN_PX, WIDE_MARGIN_PX));
        } else {
            setBorder(new EmptyBorder(NARROW_MARGIN_PX, NARROW_MARGIN_PX, NARROW_MARGIN_PX, NARROW_MARGIN_PX));
        }

        invalidate();
        repaint();
    }
}
