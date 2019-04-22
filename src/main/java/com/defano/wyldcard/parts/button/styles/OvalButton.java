package com.defano.wyldcard.parts.button.styles;

import com.defano.wyldcard.awt.DisplayInverter;
import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.parts.ToolEditablePart;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class OvalButton extends AbstractLabelButton implements DisplayInverter {

    public OvalButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
        setBorder(PartBorderFactory.createOvalBorder());
        setOpaque(false);
    }

    @Override
    protected void paintHilite(boolean isHilited, Graphics2D g) {
        if (isHilited) {
            Rectangle bounds = getBounds();
            Shape oval = new Ellipse2D.Double(bounds.x, bounds.y, bounds.width, bounds.height);
            g.drawImage(invertedPixels(oval, this), 0, 0, null);
        }
    }
}
