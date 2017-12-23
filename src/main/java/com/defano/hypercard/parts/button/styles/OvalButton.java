package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.border.OvalBorder;
import com.defano.hypercard.parts.ToolEditablePart;

import java.awt.*;

public class OvalButton extends AbstractLabelButton {

    public OvalButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(new OvalBorder());
        setOpaque(false);
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setPaint(DEFAULT_HILITE_COLOR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.fillOval(
                    getInsets().left - 1,
                    getInsets().top - 1,
                    getWidth() - getInsets().left - getInsets().right + 2,
                    getHeight() - getInsets().top - getInsets().bottom + 2
            );
        }
    }

}
