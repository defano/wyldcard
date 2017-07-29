package com.defano.hypercard.parts;

import com.defano.hypercard.parts.util.MouseEventDispatcher;
import com.defano.jmonet.canvas.JMonetCanvas;

import javax.swing.*;
import java.awt.*;

public abstract class CardLayeredPane extends JLayeredPane {

    private boolean foregroundVisible = true;
    private JMonetCanvas foregroundCanvas;
    private JMonetCanvas backgroundCanvas;
    private MouseEventDispatcher mouseEventDispatcher;

    public void setForegroundVisible(boolean isVisible) {
        foregroundVisible = isVisible;

        if (foregroundCanvas != null) {
            foregroundCanvas.setVisible(isVisible);
        }

        for (Component thisComponent : getComponentsInCardLayer(CardLayer.CARD_PARTS)) {
            thisComponent.setVisible(isVisible);
        }
    }

    public boolean isForegroundVisible() {
        return foregroundVisible;
    }

    public CardLayer getCardLayer(Component component) {
        int layer = getLayer(component);
        if (layer == DEFAULT_LAYER) {
            throw new IllegalArgumentException("Component does not exist on this card.");
        }

        return CardLayer.fromPaneLayer(layer);
    }

    public void addToCardLayer(Component component, CardLayer layer) {
        if (layer == CardLayer.CARD_GRAPHICS || layer == CardLayer.BACKGROUND_GRAPHICS) {
            throw new IllegalArgumentException("Cannot add components to the graphic layer: " + layer);
        }

        setLayer(component, layer.paneLayer);
        add(component);
    }

    public void setBackgroundCanvas(JMonetCanvas canvas) {
        if (backgroundCanvas != null) {
            remove(backgroundCanvas);
        }

        this.backgroundCanvas = canvas;
        setLayer(backgroundCanvas, CardLayer.BACKGROUND_GRAPHICS.paneLayer);
        add(backgroundCanvas);
    }

    public void setForegroundCanvas(JMonetCanvas canvas) {
        if (foregroundCanvas != null) {
            remove(foregroundCanvas);
        }

        this.foregroundCanvas = canvas;

        // Pass mouse events to parts obscured behind the canvas.
        mouseEventDispatcher = MouseEventDispatcher.bindTo(this.foregroundCanvas.getSurface(), () -> getComponentsInCardLayer(CardLayer.BACKGROUND_PARTS));

        setLayer(foregroundCanvas, CardLayer.CARD_GRAPHICS.paneLayer);
        add(foregroundCanvas);
    }

    public JMonetCanvas getBackgroundCanvas() {
        return backgroundCanvas;
    }

    public JMonetCanvas getForegroundCanvas() {
        return foregroundCanvas;
    }

    public Component[] getComponentsInCardLayer(CardLayer layer) {
        return getComponentsInLayer(layer.paneLayer);
    }

    public void dispose() {
        removeAll();

        mouseEventDispatcher.unbind();
        foregroundCanvas = null;
        backgroundCanvas = null;
    }
}
