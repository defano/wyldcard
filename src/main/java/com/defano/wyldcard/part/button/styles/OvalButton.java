package com.defano.wyldcard.part.button.styles;

import com.defano.wyldcard.awt.DisplayInverter;
import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.part.ToolEditablePart;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class OvalButton extends AbstractLabelButton implements DisplayInverter {

    public OvalButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setOpaque(false);
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            Rectangle bounds = getBounds();
            Shape oval = new Ellipse2D.Double(bounds.x, bounds.y, bounds.width, bounds.height);
            g.drawImage(invertDisplayedPixels(oval, this), 0, 0, null);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            setBorder(PartBorderFactory.createEmptyBorder());
        } else {
            setBorder(PartBorderFactory.createOvalBorder(Color.LIGHT_GRAY));
        }
    }
}
