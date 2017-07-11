package com.defano.hypercard.parts;

import com.defano.hypercard.context.ToolMode;
import com.defano.hypercard.context.ToolsContext;
import com.defano.jmonet.canvas.UndoablePaintCanvas;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public abstract class CardLayeredPane extends JLayeredPane {

    private boolean foregroundVisible = true;
    private UndoablePaintCanvas foregroundCanvas;
    private UndoablePaintCanvas backgroundCanvas;

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
        return CardLayer.fromPaneLayer(getLayer(component));
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

    /**
     * A total hack.
     *
     * This layered pane consists of four layers (back to front):
     *
     *    background paint -> background parts -> foreground graphics -> foreground parts.
     *
     * Unfortunately, the foreground graphics layer is the size of the card window; as long as it's visible (according
     * to {@link #isVisible()}) the background layers behind it will not receive mouse events or focus. We could work
     * around this by re-dispatching the mouse events, but that doesn't fix the focus problem: A field will never
     * receive focus as long as Swing believes it be fully obstructed by a component in front of it (our foreground
     * canvas, in this case).
     *
     * Swing does not support the idea of a Component that "passes" events and focus to components behind it; that's
     * what is really needed here.
     *
     * So, as a workaround, we always hide the foreground canvas, except when re-painting the card or when a paint tool
     * is active (so that it can painted on via the JMonet tools).
     *
     * @param g The paint manager's graphic context
     */
    @Override
    public void paint(Graphics g) {
        if (foregroundCanvas != null) {
            foregroundCanvas.setVisible(foregroundVisible);
            super.paint(g);
            foregroundCanvas.setVisible(foregroundVisible && ToolsContext.getInstance().getToolMode() == ToolMode.PAINT);
        }

        // No foreground canvas defined; just paint normally
        else {
            super.paint(g);
        }
    }

}
