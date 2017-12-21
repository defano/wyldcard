package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.border.DropShadowBorder;

public class ShadowField extends HyperCardTextField {

    public ShadowField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(new DropShadowBorder());
    }

}
