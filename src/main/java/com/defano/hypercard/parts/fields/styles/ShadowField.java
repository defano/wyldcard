package com.defano.hypercard.parts.fields.styles;

import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.borders.DropShadowBorder;

public class ShadowField extends AbstractTextField {

    public ShadowField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(new DropShadowBorder());
    }

}
