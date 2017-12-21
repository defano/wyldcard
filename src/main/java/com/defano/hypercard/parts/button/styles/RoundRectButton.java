package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.border.RoundRectShadowBorder;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.button.ButtonComponent;

import java.awt.*;

public class RoundRectButton extends AbstractLabelButton implements ButtonComponent {

    private final static int ARC_DIAMETER = 14;     // Rounded corner diameter

    public RoundRectButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setOpaque(false);
        setBackground(Color.WHITE);
        setBorder(new RoundRectShadowBorder(ARC_DIAMETER));
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setColor(Color.BLACK);

            // Add 1px margin to fill to prevent missed pixels around the corners
            g.fillRoundRect(
                    getInsets().left - 1,
                    getInsets().top - 1,
                    getWidth() - getInsets().left - getInsets().right + 2,
                    getHeight() - getInsets().top - getInsets().bottom + 2,
                    ARC_DIAMETER,
                    ARC_DIAMETER
            );
        }

        else {
            g.setColor(getBackground());
            g.fillRoundRect(
                    getInsets().left,
                    getInsets().top,
                    getWidth() - getInsets().left - getInsets().right,
                    getHeight() - getInsets().top - getInsets().bottom,
                    ARC_DIAMETER,
                    ARC_DIAMETER
            );
        }
    }

}