package com.defano.hypercard.parts.button;

import com.defano.hypercard.awt.KeyboardManager;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.parts.Styleable;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.button.styles.*;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.model.PropertyChangeObserver;
import com.defano.hypercard.runtime.context.FontContext;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.jmonet.tools.util.MarchingAnts;
import com.defano.jmonet.tools.util.MarchingAntsObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * The "view" object representing a styleable HyperCard button.
 * <p>
 * Note that we cannot simply extend a Swing component because the underlying component bound to this view can change at
 * runtime (i.e., a push button can morph into a radio button or combo box).
 * <p>
 * This class provides common functionality for "styleable" buttons; the actual look-and-feel of the button is provided
 * by a subclass of {@link ButtonComponent}.
 */
public abstract class StyleableButton implements Styleable<ButtonStyle,ButtonComponent>, ToolEditablePart, PropertyChangeObserver, MarchingAntsObserver {

    private final ToolModeObserver toolModeObserver = new ToolModeObserver();
    private Disposable toolModeSubscription;
    private ButtonComponent buttonComponent;
    private boolean isBeingEdited = false;
    private boolean isFocused = false;      // Indicates user pressed mouse while over part

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
            FontContext.getInstance().setFocusedTextStyle((((CardLayerPartModel) getPartModel()).getTextStyle()));
        } else {
            MarchingAnts.getInstance().removeObserver(this);
        }

        getComponent().repaint();
    }

    @Override
    public void setStyle(ButtonStyle style) {
        Component oldComponent = getButtonComponent();
        buttonComponent = getComponentForStyle(style);
        replaceViewComponent(oldComponent, (JComponent) buttonComponent);

        getPartModel().addPropertyChangedObserver(buttonComponent);
    }

    @Override
    public ButtonComponent getComponentForStyle(ButtonStyle style) {
        switch (style) {
            case CHECKBOX:
                return new CheckboxButton(this);
            case NATIVE:
                return new NativeButton(this);
            case ROUND_RECT:
                return new RoundRectButton(this);
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
            case DEFAULT:
                return new DefaultClassicButton(this);
            case SHADOW:
                return new ShadowButton(this);

            default:
                throw new IllegalArgumentException("Bug! Unimplemented button style.");
        }
    }

    @Override
    public void setComponentHierarchyEnabled(boolean enabled) {
        getComponent().setEnabled(enabled);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ToolEditablePart.super.mousePressed(e);
        this.isFocused = true;

        if (isAutoHilited()) {
            if (!(buttonComponent instanceof SharedHilight)) {
                getPartModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(true));
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        ToolEditablePart.super.mouseEntered(e);

        if (isAutoHilited() && isFocused) {
            if (!(buttonComponent instanceof SharedHilight)) {
                getPartModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(true));
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ToolEditablePart.super.mouseExited(e);

        if (isAutoHilited()) {
            if (!(buttonComponent instanceof SharedHilight)) {
                getPartModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(false));
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ToolEditablePart.super.mouseReleased(e);
        this.isFocused = false;

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
        toolModeSubscription = ToolsContext.getInstance().getToolModeProvider().subscribe(toolModeObserver);
        KeyboardManager.getInstance().addGlobalKeyListener(this);
    }

    @Override
    public void partClosed() {
        getPartModel().removePropertyChangedObserver(buttonComponent);
        KeyboardManager.getInstance().removeGlobalKeyListener(this);
        toolModeSubscription.dispose();
    }

    @Override
    public void onAntsMoved(Stroke ants) {
        SwingUtilities.invokeLater(getComponent()::repaint);
    }

    private boolean isAutoHilited() {
        return getPartModel().getKnownProperty(ButtonModel.PROP_AUTOHILIGHT).booleanValue();
    }

    private class ToolModeObserver implements Consumer<ToolMode> {
        @Override
        public void accept(ToolMode toolMode) {
            onToolModeChanged();
        }
    }
}
