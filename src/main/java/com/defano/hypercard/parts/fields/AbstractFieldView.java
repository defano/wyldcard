/*
 * AbstractFieldView
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.fields;

import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.gui.util.KeyboardManager;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.fields.styles.OpaqueField;
import com.defano.hypercard.parts.fields.styles.RectangleField;
import com.defano.hypercard.parts.fields.styles.ShadowField;
import com.defano.hypercard.parts.fields.styles.TransparentField;
import com.defano.jmonet.tools.util.MarchingAnts;
import com.defano.jmonet.tools.util.MarchingAntsObserver;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public abstract class AbstractFieldView implements ToolEditablePart, MarchingAntsObserver {

    private final ToolModeObserver toolModeObserver = new ToolModeObserver();
    private FieldView fieldView;
    private boolean isBeingEdited;

    public abstract void move();

    public abstract void resize(int fromQuadrant);

    public abstract void replaceSwingComponent(Component oldComponent, Component newComponent);

    public AbstractFieldView(FieldStyle style) {
        fieldView = getComponentForStyle(style);
    }

    @Override
    public boolean isSelectedForEditing() {
        Window ancestorWindow = SwingUtilities.getWindowAncestor(getFieldView());
        return ancestorWindow != null && ancestorWindow.isActive() && isBeingEdited;
    }

    @Override
    public void setIsSelectedForEditing(boolean beingEdited) {
        fieldView.setEditable(!beingEdited);
        isBeingEdited = beingEdited;

        isBeingEdited = beingEdited;

        if (isSelectedForEditing()) {
            MarchingAnts.getInstance().addObserver(this);
        } else {
            MarchingAnts.getInstance().removeObserver(this);
        }

        getComponent().repaint();
    }

    public void setFieldStyle(FieldStyle style) {
        Component oldComponent = getFieldView();
        fieldView = getComponentForStyle(style);
        replaceSwingComponent(oldComponent, (JComponent) fieldView);
    }

    private FieldView getComponentForStyle(FieldStyle style) {
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
    public JComponent getFieldView() {
        return (JComponent) fieldView;
    }

    public JTextComponent getTextComponent() {
        return fieldView.getTextComponent();
    }

    public String getText() {
        return fieldView.getText();
    }

    @Override
    public void partOpened() {
        fieldView.partOpened();

        getPartModel().addPropertyChangedObserver(fieldView);
        ToolsContext.getInstance().getToolModeProvider().addObserverAndUpdate(toolModeObserver);
        KeyboardManager.addGlobalKeyListener(this);
    }

    @Override
    public void partClosed() {
        fieldView.partClosed();

        getPartModel().removePropertyChangedObserver(fieldView);
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
