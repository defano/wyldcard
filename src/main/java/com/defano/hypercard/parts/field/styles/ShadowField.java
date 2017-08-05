package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.gui.border.DropShadowBorder;

public class ShadowField extends AbstractTextField {

    public ShadowField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(new DropShadowBorder());
    }

}
