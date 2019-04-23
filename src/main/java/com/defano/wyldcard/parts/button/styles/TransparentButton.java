package com.defano.wyldcard.parts.button.styles;

import com.defano.wyldcard.awt.DisplayInverter;
import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.parts.ToolEditablePart;

import java.awt.*;

public class TransparentButton extends AbstractLabelButton implements DisplayInverter {

    public TransparentButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(PartBorderFactory.createEmptyBorder());
        setOpaque(false);
    }

    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            g.drawImage(invertDisplayedPixels(getBounds(), this), 0, 0, null);
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
