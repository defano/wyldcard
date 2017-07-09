/*
 * AbstractButtonView
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.buttons;

import com.defano.hypercard.gui.util.KeyboardManager;
import com.defano.hypercard.parts.ButtonPart;
import com.defano.hypercard.parts.buttons.styles.*;
import com.defano.hypercard.parts.model.PropertyChangeObserver;
import com.defano.jmonet.tools.util.MarchingAnts;
import com.defano.jmonet.tools.util.MarchingAntsObserver;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.model.ButtonModel;
import com.defano.hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Provides common functionality for "stylable" button parts (that is, a single button part whose style determines
 * which Swing component is drawn on the card).
 */
public abstract class AbstractButtonView implements ToolEditablePart, PropertyChangeObserver, MarchingAntsObserver {

    private ButtonView buttonView;
    private boolean isBeingEdited = false;

    public abstract void move();

    public abstract void resize(int fromQuadrant);

    /**
     * Indicates that the Swing component associated with this {@link ButtonPart} has changed and that the button's
     * parent (i.e., card or background) should update itself accordingly. This is the primary means by which
     * HyperCard can swap one button style for another (in which different buttons styles are represented by
     * different Swing components).
     *
     * @param oldButtonComponent The former component associated with this part
     * @param newButtonComponent The new component
     */
    public abstract void replaceSwingComponent(Component oldButtonComponent, Component newButtonComponent);

    public AbstractButtonView(ButtonStyle style) {
        buttonView = getComponentForStyle(style);
    }

    public JComponent getButtonView() {
        return (JComponent) buttonView;
    }

    public boolean isSelectedForEditing() {
        Window ancestorWindow = SwingUtilities.getWindowAncestor(getButtonView());
        return ancestorWindow != null && ancestorWindow.isActive() && isBeingEdited;
    }

    public void setIsSelectedForEditing(boolean beingEdited) {
        isBeingEdited = beingEdited;

        if (isSelectedForEditing()) {
            MarchingAnts.getInstance().addObserver(this);
        } else {
            MarchingAnts.getInstance().removeObserver(this);
        }

        getComponent().repaint();
    }

    public void setButtonStyle(ButtonStyle style) {
        Component oldComponent = getButtonView();
        buttonView = getComponentForStyle(style);
        replaceSwingComponent(oldComponent, (JComponent) buttonView);

        getPartModel().addPropertyChangedObserver(buttonView);
        partOpened();
    }

    private ButtonView getComponentForStyle(ButtonStyle style) {
        switch (style) {
            case CHECKBOX:
                return new CheckboxButton(this);
            case DEFAULT:
                return new DefaultButton(this);
            case RADIO:
                return new RadioButton(this);
            case MENU:
                return new MenuButton(this);
            case RECTANGULAR:
                return new RectangularButton(this);
            case TRANSPARENT:
                return new TransparentButton(this);
            case OPAQUE:
                return new OpaqueButton(this);
            case OVAL:
                return new OvalButton(this);
            case CLASSIC:
                return new ClassicButton(this);
            case SHADOW:
                return new ShadowButton(this);

            default:
                throw new IllegalArgumentException("Bug! Unimplemented button style.");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ToolEditablePart.super.mousePressed(e);

        if (isAutoHilited()) {
            if (!(buttonView instanceof SharedHilight)) {
                getPartModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(true));
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ToolEditablePart.super.mouseReleased(e);

        if (!isSelectedForEditing() && isAutoHilited()) {
            if (!(buttonView instanceof SharedHilight)) {
                getPartModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(false));
            }
        }
    }

    @Override
    public void partOpened() {
        getPartModel().addPropertyChangedObserver(buttonView);
        getPartModel().notifyPropertyChangedObserver(buttonView);
        ToolsContext.getInstance().getToolModeProvider().addObserverAndUpdate((o, arg) -> onToolModeChanged());
        KeyboardManager.addGlobalKeyListener(this);
    }

    @Override
    public void partClosed() {
        KeyboardManager.removeGlobalKeyListener(this);
    }

    @Override
    public void onAntsMoved() {
        getComponent().repaint();
    }
}
