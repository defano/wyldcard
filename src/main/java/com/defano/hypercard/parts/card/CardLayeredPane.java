package com.defano.hypercard.parts.card;

import com.defano.hypercard.parts.util.MouseEventDispatcher;
import com.defano.jmonet.canvas.JMonetCanvas;

import javax.swing.*;
import java.awt.*;

public abstract class CardLayeredPane extends JLayeredPane {

    private JMonetCanvas foregroundCanvas;
    private JMonetCanvas backgroundCanvas;
    private MouseEventDispatcher mouseEventDispatcher;

    /**
     * Returns the layer of the card on which the given component exists.
     *
     * @param component The component whose layer should be determined.
     * @return The card layer of the given component.
     * @throws IllegalArgumentException if the component does not exist on this pane
     */
    public CardLayer getCardLayer(Component component) {
        int layer = getLayer(component);
        if (layer == DEFAULT_LAYER) {
            throw new IllegalArgumentException("Component does not exist on this card.");
        }

        return CardLayer.fromPaneLayer(layer);
    }

    /**
     * Adds a component to the card pane on the specified card layer.
     *
     * @param component The component to be added.
     * @param layer The layer on which the component should be added.
     */
    public void addToCardLayer(Component component, CardLayer layer) {
        if (layer == CardLayer.CARD_GRAPHICS || layer == CardLayer.BACKGROUND_GRAPHICS) {
            throw new IllegalArgumentException("Cannot add components to the graphic layer: " + layer);
        }

        setLayer(component, layer.paneLayer);
        add(component);
    }

    protected void setBackgroundCanvas(JMonetCanvas canvas) {
        if (backgroundCanvas != null) {
            remove(backgroundCanvas);
        }

        this.backgroundCanvas = canvas;
        setLayer(backgroundCanvas, CardLayer.BACKGROUND_GRAPHICS.paneLayer);
        add(backgroundCanvas);
    }

    protected void setForegroundCanvas(JMonetCanvas canvas) {
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
