package hypercard.parts.buttons;

import hypercard.parts.ToolEditablePart;
import hypercard.parts.model.ButtonModel;
import hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class AbstractButton implements ToolEditablePart {

    private ButtonComponent buttonComponent;
    private boolean isBeingEdited = false;

    public abstract void move();

    public abstract void resize(int fromQuadrant);

    public abstract void invalidateSwingComponent(Component oldButtonComponent, Component newButtonComponent);

    public AbstractButton(ButtonStyle style) {
        buttonComponent = getComponentForStyle(style);
    }

    public JComponent getButtonComponent() {
        return (JComponent) buttonComponent;
    }

    public boolean isBeingEdited() {
        return isBeingEdited;
    }

    public void setBeingEdited(boolean beingEdited) {
        isBeingEdited = beingEdited;
    }

    public void setButtonStyle(ButtonStyle style) {
        Component oldComponent = getButtonComponent();
        buttonComponent = getComponentForStyle(style);
        invalidateSwingComponent(oldComponent, (JComponent) buttonComponent);

        getPartModel().addPropertyChangedObserver(buttonComponent);
    }

    private ButtonComponent getComponentForStyle(ButtonStyle style) {
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
            if (! (buttonComponent instanceof SharedHilight)) {
                getPartModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(true));
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ToolEditablePart.super.mouseReleased(e);

        if (!isBeingEdited() && isAutoHilited()) {
            if (! (buttonComponent instanceof SharedHilight)) {
                getPartModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(false));
            }
        }
    }
}
