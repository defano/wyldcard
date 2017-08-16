package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.gui.border.DropShadowBorder;

public class ShadowField extends AbstractTextField {

    private final static int OUTLINE_STROKE = 1;
    private final static int SHADOW_STROKE = 2;
    private final static int SHADOW_OFFSET = 5;

    public ShadowField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(new DropShadowBorder(OUTLINE_STROKE, SHADOW_STROKE, SHADOW_OFFSET));
    }

}
