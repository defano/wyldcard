package com.defano.hypercard.parts.field;

import javax.swing.text.StyledDocument;

/**
 * An observer of changes to a field's document object model.
 */
public interface FieldDocumentObserver {
    /**
     * Fired to indicate the observed field's document object model has changed.
     * @param document The updated document.
     */
    void onStyledDocumentChanged(StyledDocument document);
}
