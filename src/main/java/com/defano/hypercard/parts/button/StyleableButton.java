package com.defano.hypercard.parts.button;

import com.defano.hypercard.paint.FontContext;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.awt.KeyboardManager;
import com.defano.hypercard.parts.Styleable;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.button.styles.*;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.model.PropertyChangeObserver;
import com.defano.hypertalk.ast.common.Value;
import com.defano.jmonet.tools.util.MarchingAnts;
import com.defano.jmonet.tools.util.MarchingAntsObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

/**
 * The "view" object representing a styleable HyperCard button.
 * <p>
 * Note that we cannot simply extend a Swing component because the underlying component bound to this view can change at
 * runtime (i.e., a push button can morph into a radio button or combo box).
 * <p>
 * This class provides common functionality for "stylable" button parts; the actual style of the button is provided by
 * a concrete subclass.
 */
public abstract class StyleableButton implements Styleable<ButtonStyle,ButtonComponent>, ToolEditablePart, PropertyChangeObserver, MarchingAntsObserver {

    private final ToolModeObserver toolModeObserver = new ToolModeObserver();
    private ButtonComponent buttonComponent;
    private boolean isBeingEdited = false;

    public StyleableButton(ButtonStyle style) {
        buttonComponent = getComponentForStyle(style);
    }

    public JComponent getButtonComponent() {
        return (JComponent) buttonComponent;
    }

    @Override
    public boolean isSelectedForEditing() {
        Window ancestorWindow = SwingUtilities.getWindowAncestor(getButtonComponent());
        return ancestorWindow != null && ancestorWindow.isActive() && isBeingEdited;
    }

    @Override
    public void setSelectedForEditing(boolean beingEdited) {
        isBeingEdited = beingEdited;

        if (beingEdited) {
            MarchingAnts.getInstance().addObserver(this);
            FontContext.getInstance().setHilitedTextStyle((((CardLayerPartModel) getPartModel()).getFont()));
        } else {
            MarchingAnts.getInstance().removeObserver(this);
        }

        getComponent().repaint();
    }

    @Override
    public void setStyle(ButtonStyle style) {
        Component oldComponent = getButtonComponent();
        buttonComponent = getComponentForStyle(style);
        replaceSwingComponent(oldComponent, (JComponent) buttonComponent);

        getPartModel().addPropertyChangedObserver(buttonComponent);
        partOpened();
    }

    @Override
    public ButtonComponent getComponentForStyle(ButtonStyle style) {
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
    public void setEnabledRecursively(boolean enabled) {
        getComponent().setEnabled(enabled);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ToolEditablePart.super.mousePressed(e);

        if (isAutoHilited()) {
            if (!(buttonComponent instanceof SharedHilight)) {
                getPartModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(true));
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ToolEditablePart.super.mouseReleased(e);

        if (!isSelectedForEditing() && isAutoHilited()) {
            if (!(buttonComponent instanceof SharedHilight)) {
                getPartModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(false));
            }
        }
    }

    @Override
    public void partOpened() {
        getPartModel().addPropertyChangedObserver(buttonComponent);
        getPartModel().notifyPropertyChangedObserver(buttonComponent);
        ToolsContext.getInstance().getToolModeProvider().addObserverAndUpdate(toolModeObserver);
        KeyboardManager.addGlobalKeyListener(this);
    }

    @Override
    public void partClosed() {
        getPartModel().removePropertyChangedObserver(buttonComponent);
        KeyboardManager.removeGlobalKeyListener(this);
        ToolsContext.getInstance().getToolModeProvider().deleteObserver(toolModeObserver);
    }

    @Override
    public void onAntsMoved() {
        getComponent().repaint();
    }

    private boolean isAutoHilited() {
        return getPartModel().getKnownProperty(ButtonModel.PROP_AUTOHILIGHT).booleanValue();
    }

    private class ToolModeObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            onToolModeChanged();
        }
    }
}
