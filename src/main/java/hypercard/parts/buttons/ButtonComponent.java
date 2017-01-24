package hypercard.parts.buttons;

import hypercard.parts.model.PropertyChangeObserver;

import javax.swing.*;

public interface ButtonComponent extends PropertyChangeObserver {
    boolean hasSharedHilite();
}
