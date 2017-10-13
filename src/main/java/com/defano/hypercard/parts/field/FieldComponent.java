package com.defano.hypercard.parts.field;

import com.defano.hypercard.parts.model.PropertyChangeObserver;

import javax.swing.*;

public interface FieldComponent extends PropertyChangeObserver {

    /**
     * Gets the JTextPane portion of this component. Field components are generally comprised of a JScrollPane and a
     * JTextPage; this gets the later component.
     *
     * @return The JTextPane
     */
    JTextPane getTextPane();

    /**
     * Sets whether the text in this component should be user editable.
     *
     * @param editable Make text editable when true
     */
    void setEditable(boolean editable);

    /**
     * Invoked to indicate the part has opened on the card.
     */
    void partOpened();

    /**
     * Invoked to indicate the part has closed on the card.
     */
    void partClosed();
}
