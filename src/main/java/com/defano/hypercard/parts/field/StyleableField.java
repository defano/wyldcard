package com.defano.hypercard.parts.field;

import com.defano.hypercard.awt.KeyboardManager;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.parts.Styleable;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.field.styles.*;
import com.defano.hypercard.runtime.context.FontContext;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.jmonet.tools.util.MarchingAnts;
import com.defano.jmonet.tools.util.MarchingAntsObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import javax.swing.*;
import java.awt.*;

/**
 * The "view" object representing a styleable HyperCard field.
 * <p>
 * Note that we cannot simply extend a Swing component because the underlying component bound to this view can change at
 * runtime (i.e., a transparent field can morph into a rectangular one).
 * <p>
 * This class provides common functionality for "styleable" field parts; the actual style of the field is provided by
 * a concrete subclass.
 */
public abstract class StyleableField implements Styleable<FieldStyle,FieldComponent>, ToolEditablePart, MarchingAntsObserver {

    private final ToolModeObserver toolModeObserver = new ToolModeObserver();
    private Disposable toolModeSubscription;
    private FieldComponent fieldComponent;
    private boolean isBeingEdited;

    public StyleableField(FieldStyle style) {
        fieldComponent = getComponentForStyle(style);
    }

    @Override
    public boolean isSelectedForEditing() {
        Window ancestorWindow = SwingUtilities.getWindowAncestor(getFieldComponent());
        return ancestorWindow != null && ancestorWindow.isActive() && isBeingEdited;
    }

    @Override
    public void setSelectedForEditing(boolean beingEdited) {
        isBeingEdited = beingEdited;

        if (isSelectedForEditing()) {
            MarchingAnts.getInstance().addObserver(this);

            // TODO: Focus style only reflects first char; should reflect entire field
            FontContext.getInstance().setFocusedTextStyle((((CardLayerPartModel) getPartModel()).getTextStyle()));
        } else {
            MarchingAnts.getInstance().removeObserver(this);
        }

        getComponent().repaint();
    }

    @Override
    public void setStyle(FieldStyle style) {
        Component oldComponent = getFieldComponent();
        fieldComponent = getComponentForStyle(style);
        replaceViewComponent(oldComponent, (JComponent) fieldComponent);
    }

    @Override
    public FieldComponent getComponentForStyle(FieldStyle style) {
        switch (style) {
            case TRANSPARENT:
                return new TransparentField(this);
            case OPAQUE:
                return new OpaqueField(this);
            case SHADOW:
                return new ShadowField(this);
            case RECTANGLE:
                return new RectangleField(this);

            default:
                throw new IllegalArgumentException("No such field style: " + style);
        }
    }

    /**
     * Gets the Swing component representing the field as a whole; this is typically a JTextComponent plus some other
     * hierarchy (like a scroll pane).
     *
     * @return
     */
    public JComponent getFieldComponent() {
        return (JComponent) fieldComponent;
    }

    public HyperCardTextPane getHyperCardTextPane() {
        return fieldComponent.getTextPane();
    }

    @Override
    public void setEnabledRecursively(boolean enabled) {
        getComponent().setEnabled(enabled);
        getHyperCardTextPane().setEnabled(enabled);
    }

    @Override
    public void partOpened() {
        fieldComponent.partOpened();

        getPartModel().addPropertyChangedObserver(fieldComponent);
        toolModeSubscription = ToolsContext.getInstance().getToolModeProvider().subscribe(toolModeObserver);
        KeyboardManager.addGlobalKeyListener(this);
    }

    @Override
    public void partClosed() {
        fieldComponent.partClosed();

        getPartModel().removePropertyChangedObserver(fieldComponent);
        KeyboardManager.removeGlobalKeyListener(this);
        toolModeSubscription.dispose();
    }

    @Override
    public void onAntsMoved() {
        SwingUtilities.invokeLater(getFieldComponent()::repaint);
    }

    private class ToolModeObserver implements Consumer<ToolMode> {
        @Override
        public void accept(ToolMode toolMode) {
            onToolModeChanged();
        }
    }
}
