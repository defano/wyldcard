package com.defano.wyldcard.parts.field.styles;

import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.parts.ToolEditablePart;

public class ShadowField extends HyperCardTextField {

    public ShadowField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(PartBorderFactory.createDropShadowBorder());
    }

}
