package hypercard.parts.fields;

import hypercard.context.ToolsContext;
import hypercard.gui.util.*;
import hypercard.parts.ToolEditablePart;
import hypercard.parts.fields.styles.OpaqueField;
import hypercard.parts.fields.styles.TransparentField;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public abstract class AbstractStylableField implements ToolEditablePart {

    private FieldComponent fieldComponent;
    private boolean isBeingEdited;

    public abstract void move();

    public abstract void resize(int fromQuadrant);

    public abstract void invalidateSwingComponent(Component oldComponent, Component newComponent);

    public AbstractStylableField(FieldStyle style) {
        fieldComponent = getComponentForStyle(style);
    }

    @Override
    public boolean isBeingEdited() {
        Window ancestorWindow = SwingUtilities.getWindowAncestor(getFieldComponent());
        return ancestorWindow != null && ancestorWindow.isActive() && isBeingEdited;
    }

    @Override
    public void setBeingEdited(boolean beingEdited) {
        fieldComponent.setEditable(!beingEdited);
        isBeingEdited = beingEdited;
    }

    public void setFieldStyle(FieldStyle style) {
        Component oldComponent = getFieldComponent();
        fieldComponent = getComponentForStyle(style);

        partClosed();
        invalidateSwingComponent(oldComponent, (JComponent) fieldComponent);
        partOpened();
    }

    private FieldComponent getComponentForStyle(FieldStyle style) {
        switch (style) {
            case TRANSPARENT:
                return new TransparentField(this);
            case OPAQUE:
                return new OpaqueField(this);

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

    public JTextComponent getTextComponent() {
        return fieldComponent.getTextComponent();
    }

    public String getText() {
        return fieldComponent.getText();
    }

    @Override
    public void partOpened() {
        getPartModel().addPropertyChangedObserver(fieldComponent);
        ToolsContext.getInstance().getToolModeProvider().addObserverAndUpdate((o, arg) -> onToolModeChanged());
        KeyboardManager.addGlobalKeyListener(this);

        fieldComponent.partOpened();
    }

    @Override
    public void partClosed() {
        getPartModel().removePropertyChangedObserver(fieldComponent);
        KeyboardManager.removeGlobalKeyListener(this);
    }

}
