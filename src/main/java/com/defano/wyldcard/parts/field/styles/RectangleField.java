package com.defano.wyldcard.parts.field.styles;

import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.parts.ToolEditablePart;

public class RectangleField extends HyperCardTextField {
    public RectangleField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
    }

    @Override
    protected void setWideMargins(boolean isWideMargins) {
        if (isWideMargins) {
            getTextPane().setBorder(PartBorderFactory.createEmptyBorder(WIDE_MARGIN_PX));
        } else {
            getTextPane().setBorder(PartBorderFactory.createEmptyBorder(NARROW_MARGIN_PX));
        }

        invalidate();
        repaint();
    }

}
