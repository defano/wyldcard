package com.defano.wyldcard.part.button.styles;

import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.part.ToolEditablePart;
import com.defano.wyldcard.part.button.HyperCardButton;

import java.awt.*;

public class RoundRectButton extends AbstractLabelButton implements HyperCardButton {

    private static final int ARC_DIAMETER = 14;     // Rounded corner diameter

    public RoundRectButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setOpaque(false);
        setBackground(Color.WHITE);
        setBorder(PartBorderFactory.createRoundRectShadowBorder(ARC_DIAMETER));
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setColor(Color.BLACK);

            // Expand fill area into the border to prevent unfilled/aliased pixels in the corners
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