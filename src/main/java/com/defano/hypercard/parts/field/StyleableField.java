/*
 * AbstractFieldView
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.field;

import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.awt.KeyboardManager;
import com.defano.hypercard.parts.Styleable;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.field.styles.OpaqueField;
import com.defano.hypercard.parts.field.styles.RectangleField;
import com.defano.hypercard.parts.field.styles.ShadowField;
import com.defano.hypercard.parts.field.styles.TransparentField;
import com.defano.jmonet.tools.util.MarchingAnts;
import com.defano.jmonet.tools.util.MarchingAntsObserver;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

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
        } else {
            MarchingAnts.getInstance().removeObserver(this);
        }

        getComponent().repaint();
    }

    @Override
    public void setStyle(FieldStyle style) {
        Component oldComponent = getFieldComponent();
        fieldComponent = getComponentForStyle(style);
        replaceSwingComponent(oldComponent, (JComponent) fieldComponent);
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

    public JTextPane getTextPane() {
        return fieldComponent.getTextPane();
    }

    @Override
    public void setEnabledRecursively(boolean enabled) {
        getComponent().setEnabled(enabled);
        getTextPane().setEnabled(enabled);
    }

    @Override
    public void partOpened() {
        getPartModel().addPropertyChangedObserver(fieldComponent);
        ToolsContext.getInstance().getToolModeProvider().addObserverAndUpdate(toolModeObserver);
        KeyboardManager.addGlobalKeyListener(this);

        fieldComponent.partOpened();
    }

    @Override
    public void partClosed() {
        fieldComponent.partClosed();

        getPartModel().removePropertyChangedObserver(fieldComponent);
        KeyboardManager.removeGlobalKeyListener(this);
        ToolsContext.getInstance().getToolModeProvider().deleteObserver(toolModeObserver);
    }

    @Override
    public void onAntsMoved() {
        getComponent().repaint();
    }

    private class ToolModeObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            onToolModeChanged();
        }
    }
}
