package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.border.PartBorderFactory;
import com.defano.hypercard.parts.ToolEditablePart;

import javax.swing.border.Border;

public class DefaultClassicButton extends ClassicButton {

    private final static int INNER_BORDER_WIDTH = 1;
    private final static int BORDER_SEPARATION = 1;
    private final static int OUTER_BORDER_WIDTH = 4;

    public DefaultClassicButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
    }

    @Override
    protected Border getButtonBorder() {
        return PartBorderFactory.createDoubleRoundRectBorder(INNER_BORDER_WIDTH, getButtonCornerDiameter(), BORDER_SEPARATION, OUTER_BORDER_WIDTH, getButtonCornerDiameter() * 2);
    }
}
