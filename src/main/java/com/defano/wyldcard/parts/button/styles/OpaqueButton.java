package com.defano.wyldcard.parts.button.styles;

import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.parts.ToolEditablePart;

import java.awt.*;

public class OpaqueButton extends AbstractLabelButton {

    public OpaqueButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(PartBorderFactory.createEmptyBorder());
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setPaint(Color.BLACK);
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
