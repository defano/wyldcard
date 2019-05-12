package com.defano.wyldcard.part.field;

import com.defano.wyldcard.part.model.PropertyChangeObserver;
import com.defano.hypertalk.util.Range;

import javax.swing.text.StyledDocument;
import java.util.Set;

/**
 * An observer of changes to properties of a field that are not modeled as addressable HyperTalk properties (and
 * therefore are not observed using the more typical
 * {@link com.defano.wyldcard.property.PropertiesModel#addPropertyChangedObserver(PropertyChangeObserver)}.
 */
public interface FieldModelObserver {
    /**
     * Fired to indicate the observed field's document object model has changed.
     * @param document The updated document.
     */
    void onStyledDocumentChanged(StyledDocument document);

    /**
     * Fired to indicate that the set of auto-selected lines (that is, those highlighted in a "list field") have
     * changed.
     *
     * @param selectedLines The set of list-highlighted lines.
     */
    void onAutoSelectionChanged(Set<Integer> selectedLines);

    /**
     * Fired to indicate the normal (cursor-highlighted) selection of the field has changed.
     * @param selection The new, cursor selection.
     */
    void onSelectionChange(Range selection);
}
