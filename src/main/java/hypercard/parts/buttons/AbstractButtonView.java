package hypercard.parts.buttons;

import hypercard.context.ToolsContext;
import hypercard.gui.util.*;
import hypercard.parts.ToolEditablePart;
import hypercard.parts.buttons.styles.*;
import hypercard.parts.model.ButtonModel;
import hypercard.parts.model.PropertyChangeObserver;
import hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Provides common functionality for "stylable" button parts (that is, a single button part whose style determines
 * which Swing component is drawn on the card).
 */
public abstract class AbstractButtonView implements ToolEditablePart, PropertyChangeObserver {

    private ButtonView buttonView;
    private boolean isBeingEdited = false;

    public abstract void move();

    public abstract void resize(int fromQuadrant);

    public abstract void invalidateSwingComponent(Component oldButtonComponent, Component newButtonComponent);

    public AbstractButtonView(ButtonStyle style) {
        buttonView = getComponentForStyle(style);
    }

    public JComponent getButtonView() {
        return (JComponent) buttonView;
    }

    public boolean isBeingEdited() {
        Window ancestorWindow = SwingUtilities.getWindowAncestor(getButtonView());
        return ancestorWindow != null && ancestorWindow.isActive() && isBeingEdited;
    }

    public void setBeingEdited(boolean beingEdited) {
        isBeingEdited = beingEdited;
    }

    public void setButtonStyle(ButtonStyle style) {
        Component oldComponent = getButtonView();
        buttonView = getComponentForStyle(style);
        invalidateSwingComponent(oldComponent, (JComponent) buttonView);

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
            case OVAL:
                return new OvalButton(this);
            case CLASSIC:
                return new ClassicButton(this);

            default:
                throw new IllegalArgumentException("Bug! Unimplemented button style.");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ToolEditablePart.super.mousePressed(e);

        if (isAutoHilited()) {
            if (! (buttonView instanceof SharedHilight)) {
                getPartModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(true));
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ToolEditablePart.super.mouseReleased(e);

        if (!isBeingEdited() && isAutoHilited()) {
            if (! (buttonView instanceof SharedHilight)) {
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
}
