package com.defano.wyldcard.parts.field.styles;

import com.defano.wyldcard.border.DropShadowBorder;
import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.parts.ToolEditablePart;

public class ShadowField extends HyperCardTextField {

    public ShadowField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(PartBorderFactory.createDropShadowBorder());
    }

    @Override
    protected void setWideMargins(boolean isWideMargins) {
        DropShadowBorder shadowBorder = new DropShadowBorder();
        shadowBorder.setMargin(isWideMargins ? WIDE_MARGIN_PX : NARROW_MARGIN_PX);
        setBorder(shadowBorder);

        invalidate();
        repaint();
    }

}
