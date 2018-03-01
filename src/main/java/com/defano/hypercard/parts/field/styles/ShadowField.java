package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.border.PartBorderFactory;
import com.defano.hypercard.parts.ToolEditablePart;

public class ShadowField extends HyperCardTextField {

    public ShadowField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(PartBorderFactory.createDropShadowBorder());
    }

}
