package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.border.PartBorderFactory;
import com.defano.hypercard.parts.ToolEditablePart;

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

}
