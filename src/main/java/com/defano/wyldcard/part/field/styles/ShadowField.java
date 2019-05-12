package com.defano.wyldcard.part.field.styles;

import com.defano.wyldcard.border.DropShadowBorder;
import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.part.ToolEditablePart;

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
