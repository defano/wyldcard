package com.defano.hypercard.parts;

import com.defano.hypercard.parts.util.MouseEventDispatcher;
import com.defano.jmonet.canvas.UndoablePaintCanvas;

import javax.swing.*;
import java.awt.*;

public abstract class CardLayeredPane extends JLayeredPane {

    private boolean foregroundVisible = true;
    private UndoablePaintCanvas foregroundCanvas;
    private UndoablePaintCanvas backgroundCanvas;

    public CardLayeredPane() {
    }

    public void setForegroundVisible(boolean isVisible) {
        foregroundVisible = isVisible;
        foregroundCanvas.setVisible(isVisible);

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

    public void setBackgroundCanvas(UndoablePaintCanvas canvas) {
        if (backgroundCanvas != null) {
            remove(backgroundCanvas);
        }

        this.backgroundCanvas = canvas;
        setLayer(backgroundCanvas, CardLayer.BACKGROUND_GRAPHICS.paneLayer);
        add(backgroundCanvas);
    }

    public void setForegroundCanvas(UndoablePaintCanvas canvas) {
        if (foregroundCanvas != null) {
            remove(foregroundCanvas);
        }

        this.foregroundCanvas = canvas;

        // Pass mouse events to parts obscured behind the canvas.
        MouseEventDispatcher.bindTo(this.foregroundCanvas.getSurface(), () -> getComponentsInCardLayer(CardLayer.BACKGROUND_PARTS));

        setLayer(foregroundCanvas, CardLayer.CARD_GRAPHICS.paneLayer);
        add(foregroundCanvas);
    }

    public UndoablePaintCanvas getBackgroundCanvas() {
        return backgroundCanvas;
    }

    public UndoablePaintCanvas getForegroundCanvas() {
        return foregroundCanvas;
    }

    public Component[] getComponentsInCardLayer(CardLayer layer) {
        return getComponentsInLayer(layer.paneLayer);
    }
}
