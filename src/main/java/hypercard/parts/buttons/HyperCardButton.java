package hypercard.parts.buttons;

import javax.swing.*;
import java.awt.*;

public abstract class HyperCardButton implements ToolEditablePart {

    public abstract void move();

    public abstract void resize(int fromQuadrant);

    public abstract void invalidateButtonComponent(Component oldButtonComponent, Component newButtonComponent);

    private AbstractButton buttonComponent;
    private boolean isSelected = false;

    public HyperCardButton(ButtonStyle style) {
        buttonComponent = getComponentForStyle(style);
    }

    public AbstractButton getButtonComponent() {
        return buttonComponent;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setButtonStyle(ButtonStyle style) {
        AbstractButton oldComponent = getButtonComponent();

        buttonComponent = getComponentForStyle(style);
        buttonComponent.setText(oldComponent.getText());

        invalidateButtonComponent(oldComponent, buttonComponent);
    }

    private AbstractButton getComponentForStyle(ButtonStyle style) {
        switch (style) {
            case CHECKBOX:
                return new CheckboxButton(this);
            case DEFAULT:
                return new DefaultButton(this);
            case RADIO:
                return new RadioButton(this);
            case MENU:
                return new MenuButton(this);

            default:
                throw new IllegalArgumentException("Bug! Unimplemented button style.");
        }
    }
}
