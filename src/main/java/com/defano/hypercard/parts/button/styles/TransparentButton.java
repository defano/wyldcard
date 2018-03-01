package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.border.PartBorderFactory;
import com.defano.hypercard.parts.ToolEditablePart;

import java.awt.*;

public class TransparentButton extends AbstractLabelButton {

    public TransparentButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(PartBorderFactory.createEmptyBorder());
        setOpaque(false);
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setPaint(DEFAULT_HILITE_COLOR);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            setBorder(PartBorderFactory.createEmptyBorder());
        } else {
            setBorder(PartBorderFactory.createLineBorder());
        }
    }
}
