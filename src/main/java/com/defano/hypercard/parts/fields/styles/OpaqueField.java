/*
 * OpaqueField
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.fields.styles;

import com.defano.hypercard.parts.CardPane;
import com.defano.hypercard.parts.ToolEditablePart;

import java.awt.*;

public class OpaqueField extends AbstractTextField {

    public OpaqueField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
    }

    @Override
    public void paint(Graphics g) {

        /*
         * This bit of weirdness draws the component with a transparent background at all times except when the layer
         * in which its placed is being drawn. This allows graphics and whatnot in subsequent layers to overlay the
         * field; without this, when placed in the background of a card, the part will still obscure graphics in
         * the foreground layer.
         */

        Component parent = getParent();
        if (parent instanceof CardPane) {
            boolean isLayerDrawing = ((CardPane) parent).isComponentsCardLayerDrawing(this);
            setOpaque(isLayerDrawing);
        }

        super.paint(g);
        setOpaque(false);
    }

    /**
     * Sets the opaque property on the entire view hierarchy of this component.
     * @param opaque When true, field is drawn with an opaque (white) background.
     */
    @Override
    public void setOpaque(boolean opaque) {
        super.setOpaque(opaque);

        if (textPane != null) {
            textPane.setOpaque(opaque);
        }

        if (getViewport() != null) {
            getViewport().setOpaque(opaque);
        }
    }
}
