package hypercard.parts.fields;

import hypercard.parts.model.PropertyChangeObserver;

import javax.swing.text.JTextComponent;

public interface FieldView extends PropertyChangeObserver {
    String getText();
    JTextComponent getTextComponent();
    void setEditable(boolean editable);
    void partOpened();
}
