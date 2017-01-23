package hypercard.parts.buttons;

import hypercard.parts.ToolEditablePart;
import hypercard.parts.PartTextChangeObserver;
import hypercard.parts.model.*;
import hypercard.parts.model.ButtonModel;
import hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public abstract class HyperCardButton implements ToolEditablePart {

    public abstract void move();

    public abstract void resize(int fromQuadrant);

    public abstract void invalidateButtonComponent(Component oldButtonComponent, Component newButtonComponent);

    private JComponent buttonComponent;
    private boolean isBeingEdited = false;
    private String name = "";
    private ArrayList<PartTextChangeObserver> textChangeObservers = new ArrayList();

    public HyperCardButton(ButtonStyle style) {
        buttonComponent = getComponentForStyle(style);
    }

    public JComponent getButtonComponent() {
        return buttonComponent;
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
        invalidateButtonComponent(oldComponent, buttonComponent);
        fireTextChangeObservers(getName());
    }

    private JComponent getComponentForStyle(ButtonStyle style) {
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

            default:
                throw new IllegalArgumentException("Bug! Unimplemented button style.");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ToolEditablePart.super.mousePressed(e);

        if (isAutoHilited()) {
            getModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(true));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ToolEditablePart.super.mouseReleased(e);

        if (isAutoHilited()) {
            getModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(false));
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String text) {
        this.name = text;
        fireTextChangeObservers(text);
    }

    @Override
    public void addTextChangeObserver(PartTextChangeObserver observer) {
        textChangeObservers.add(observer);
    }

    private boolean isAutoHilited() {
        return getModel().getKnownProperty(ButtonModel.PROP_AUTOHILIGHT).booleanValue();
    }

    private void fireTextChangeObservers(String newText) {
        for (PartTextChangeObserver thisObserver : textChangeObservers) {
            thisObserver.onTextChanged(newText);
        }
    }
}
