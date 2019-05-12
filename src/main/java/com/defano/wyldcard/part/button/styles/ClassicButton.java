package com.defano.wyldcard.part.button.styles;

import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.part.ToolEditablePart;
import com.defano.wyldcard.part.button.HyperCardButton;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ClassicButton extends AbstractLabelButton implements HyperCardButton {

    private final static int ARC_DIAMETER = 6;      // Rounded corner diameter

    public ClassicButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setOpaque(false);
        setBackground(Color.WHITE);
        setBorder(getButtonBorder());
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {

        g.setColor(getBackground());
        g.fill(new RoundRectangle2D.Double(
                getInsets().left,
                getInsets().top,
                getWidth() - getInsets().left - getInsets().right,
                getHeight() - getInsets().top - getInsets().bottom,
                ARC_DIAMETER,
                ARC_DIAMETER
        ));

        if (isHilited) {
            g.setColor(Color.BLACK);
            g.fill(new RoundRectangle2D.Double(
                    getInsets().left,
                    getInsets().top,
                    getWidth() - getInsets().left - getInsets().right,
                    getHeight() - getInsets().top - getInsets().bottom,
                    ARC_DIAMETER,
                    ARC_DIAMETER
            ));
        }
    }

    protected int getButtonCornerDiameter() {
        return ARC_DIAMETER;
    }

    protected Border getButtonBorder() {
        return PartBorderFactory.createRoundRectBorder(ARC_DIAMETER);
    }
}
