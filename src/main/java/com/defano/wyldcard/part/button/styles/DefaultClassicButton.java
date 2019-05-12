package com.defano.wyldcard.part.button.styles;

import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.part.ToolEditablePart;

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
