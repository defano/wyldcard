package com.defano.hypercard.parts;

import javax.swing.*;
import java.awt.*;

public abstract class CardPane extends JLayeredPane {

    private CardLayer layerBeingDrawn;

    public CardLayer getCardLayer(Component component) {
        return CardLayer.fromPaneLayer(getLayer(component));
    }

    public void addToLayer(Component component, CardLayer layer) {
        setLayer(component, layer.paneLayer);
        add(component);
    }

    public Component[] getComponentsInLayer(CardLayer layer) {
        return getComponentsInLayer(layer.paneLayer);
    }

    public boolean isComponentsCardLayerDrawing(Component c) {
        return getCardLayer(c) == layerBeingDrawn;
    }

    @Override
    public void paint(Graphics g) {
        for (CardLayer thisLayer : CardLayer.inDrawOrder()) {
            layerBeingDrawn = thisLayer;
            paintComponentsInLayer(g, thisLayer.paneLayer);
        }

        layerBeingDrawn = null;
    }

    private void paintComponentsInLayer(Graphics g, int layer) {
        // TODO: Need parts in Z-Order

        for (Component c : getComponentsInLayer(layer)) {
            Graphics g2 = g.create();
            g2.translate(c.getX(), c.getY());
            c.paintAll(g2);
            g2.dispose();
        }
    }

}
