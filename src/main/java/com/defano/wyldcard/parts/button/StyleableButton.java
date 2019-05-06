package com.defano.wyldcard.parts.button;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.parts.Styleable;
import com.defano.wyldcard.parts.ToolEditablePart;
import com.defano.wyldcard.parts.button.styles.*;
import com.defano.wyldcard.parts.model.PropertyChangeObserver;
import com.defano.wyldcard.runtime.ExecutionContext;
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
 * by a subclass of {@link HyperCardButton}.
 */
public abstract class StyleableButton implements Styleable<ButtonStyle,HyperCardButton>, ToolEditablePart<ButtonModel>, PropertyChangeObserver, MarchingAntsObserver {

    private final ToolModeObserver toolModeObserver = new ToolModeObserver();
    private Disposable toolModeSubscription;
    private HyperCardButton buttonComponent;
    private boolean isBeingEdited = false;  // Indicates part is selected for editing (has marching ants)
    private boolean isFocused = false;      // Indicates user pressed mouse while over part

    public StyleableButton(ButtonStyle style) {
        buttonComponent = getComponentForStyle(style);
        buttonComponent.onStart();
    }

    public JComponent getButtonComponent() {
        return (JComponent) buttonComponent;
    }

    @Override
    @RunOnDispatch
    public boolean isSelectedForEditing() {
        Window ancestorWindow = SwingUtilities.getWindowAncestor(getButtonComponent());
        return ancestorWindow != null && ancestorWindow.isActive() && isBeingEdited;
    }

    @Override
    @RunOnDispatch
    public void setSelectedForEditing(ExecutionContext context, boolean beingEdited) {
        isBeingEdited = beingEdited;

        if (beingEdited) {
            MarchingAnts.getInstance().addObserver(this);
            WyldCard.getInstance().getFontManager().setFocusedTextStyle((getPartModel().getTextStyle(context)));
        } else {
            MarchingAnts.getInstance().removeObserver(this);
        }

        getComponent().repaint();
    }

    @Override
    @RunOnDispatch
    public void setStyle(ExecutionContext context, ButtonStyle style) {
        Component oldComponent = getButtonComponent();

        if (buttonComponent != null) {
            buttonComponent.onStop();
            getPartModel().removePropertyChangedObserver(buttonComponent);
        }

        buttonComponent = getComponentForStyle(style);
        buttonComponent.onStart();

        replaceViewComponent(context, oldComponent, (JComponent) buttonComponent);
        getPartModel().addPropertyChangedObserver(buttonComponent);
    }

    @Override
    @RunOnDispatch
    public HyperCardButton getComponentForStyle(ButtonStyle style) {
        switch (style) {
            case CHECKBOX:
                return new CheckboxButton(this);
            case NATIVE:
                return new NativeButton(this);
            case ROUND_RECT:
                return new RoundRectButton(this);
            case RADIO:
                return new RadioButton(this);
            case POPUP:
                return new PopupButton(this);
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
    @RunOnDispatch
    public void setComponentHierarchyEnabled(boolean enabled) {
        getComponent().setEnabled(enabled);
    }

    @Override
    @RunOnDispatch
    public void mousePressed(MouseEvent e) {
        ToolEditablePart.super.mousePressed(e);
        this.isFocused = true;

        if (isAutoHilited()) {
            if (!(buttonComponent instanceof SharedHilite)) {
                getPartModel().set(new ExecutionContext(), ButtonModel.ALIAS_HILITE, new Value(true));
            }
        }
    }

    @Override
    @RunOnDispatch
    public void mouseEntered(MouseEvent e) {
        ToolEditablePart.super.mouseEntered(e);

        if (e.getButton() == 0) {
            isFocused = false;
        }

        if (isAutoHilited() && isFocused) {
            if (!(buttonComponent instanceof SharedHilite)) {
                getPartModel().set(new ExecutionContext(), ButtonModel.ALIAS_HILITE, new Value(true));
            }
        }
    }

    @Override
    @RunOnDispatch
    public void mouseExited(MouseEvent e) {
        ToolEditablePart.super.mouseExited(e);

        if (isAutoHilited()) {
            if (!(buttonComponent instanceof SharedHilite)) {
                getPartModel().set(new ExecutionContext(), ButtonModel.ALIAS_HILITE, new Value(false));
            }
        }
    }

    @Override
    @RunOnDispatch
    public void mouseReleased(MouseEvent e) {
        ToolEditablePart.super.mouseReleased(e);
        this.isFocused = false;

        if (!isSelectedForEditing() && isAutoHilited()) {
            if (!(buttonComponent instanceof SharedHilite)) {
                getPartModel().set(new ExecutionContext(), ButtonModel.ALIAS_HILITE, new Value(false));
            }
        }
    }

    @Override
    @RunOnDispatch
    public void partOpened(ExecutionContext context) {
        getPartModel().addPropertyChangedObserverAndNotify(context, buttonComponent);
        toolModeSubscription = WyldCard.getInstance().getToolsManager().getToolModeProvider().subscribe(toolModeObserver);
        WyldCard.getInstance().getKeyboardManager().addGlobalKeyListener(this);
    }

    @Override
    @RunOnDispatch
    public void partClosed(ExecutionContext context) {
        getPartModel().removePropertyChangedObserver(buttonComponent);
        WyldCard.getInstance().getKeyboardManager().removeGlobalKeyListener(this);
        toolModeSubscription.dispose();
    }

    @Override
    public void onAntsMoved(Stroke ants) {
        SwingUtilities.invokeLater(getComponent()::repaint);
    }

    private boolean isAutoHilited() {
        return getPartModel().get(new ExecutionContext(), ButtonModel.ALIAS_AUTOHILIGHT).booleanValue();
    }

    private class ToolModeObserver implements Consumer<ToolMode> {
        @Override
        public void accept(ToolMode toolMode) {
            onToolModeChanged(new ExecutionContext());
        }
    }
}
