package hypercard.parts.fields;

import hypercard.parts.model.PropertyChangeObserver;

public interface FieldComponent extends PropertyChangeObserver {
    String getText();
    void setEditable(boolean editable);
}
