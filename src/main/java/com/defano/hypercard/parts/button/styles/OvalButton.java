package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.border.OvalBorder;
import com.defano.hypercard.parts.ToolEditablePart;

import java.awt.*;

public class OvalButton extends AbstractLabelButton {

    private final static int OUTLINE_STROKE = 1;    // Width of button outline

    public OvalButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(new OvalBorder(OUTLINE_STROKE));
        setOpaque(false);
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setPaint(DEFAULT_HILITE_COLOR);
            g.fillOval(
                    getInsets().left,
                    getInsets().top,
                    getWidth() - getInsets().left - getInsets().right,
                    getHeight() - getInsets().top - getInsets().bottom
            );
        }
    }

}
