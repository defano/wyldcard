package com.defano.wyldcard.part.button.styles;

import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.part.ToolEditablePart;

import java.awt.*;

public class ShadowButton extends AbstractLabelButton {

    private final static int HILITE_INSET = 1;      // Inset of fill-hilite

    public ShadowButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(PartBorderFactory.createDropShadowBorder());
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        g.setPaint(getBackground());
        g.fillRect(
                getInsets().left,
                getInsets().top,
                getWidth() - getInsets().left - getInsets().right,
                getHeight() - getInsets().top - getInsets().bottom
        );

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
