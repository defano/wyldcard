package com.defano.hypercard.parts.button.styles;

import com.defano.hypercard.border.RectangleBorder;
import com.defano.hypercard.parts.ToolEditablePart;

import java.awt.*;

public class RectangularButton extends AbstractLabelButton {

    private final static int OUTLINE_STROKE = 1;        // Width of button outline
    private final static int HILITE_INSET = 1;          // Inset of fill hilite

    public RectangularButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(new RectangleBorder(OUTLINE_STROKE));
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.setPaint(Color.BLACK);
            g.fillRect(
                    getInsets().left + HILITE_INSET,
                    getInsets().top + HILITE_INSET,
                    getWidth() - getInsets().left - getInsets().right - HILITE_INSET * 2,
                    getHeight() - getInsets().top - getInsets().bottom - HILITE_INSET * 2
            );
        }
    }

}
