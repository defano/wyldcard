package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.border.RoundRectBorder;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.button.ButtonComponent;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ClassicButton extends AbstractLabelButton implements ButtonComponent {

    protected final static int ARC_DIAMETER = 6;      // Rounded corner diameter
    private final static int HILITE_INSET = 1;

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
                    getInsets().left + HILITE_INSET,
                    getInsets().top + HILITE_INSET,
                    getWidth() - getInsets().left - getInsets().right - HILITE_INSET * 2,
                    getHeight() - getInsets().top - getInsets().bottom - HILITE_INSET * 2,
                    ARC_DIAMETER,
                    ARC_DIAMETER
            ));
        }
    }

    protected Border getButtonBorder() {
        return new RoundRectBorder(ARC_DIAMETER);
    }
}
