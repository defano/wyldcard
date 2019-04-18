package com.defano.wyldcard.parts.field;

import com.defano.wyldcard.parts.model.PropertyChangeObserver;
import com.defano.hypertalk.utils.Range;

import javax.swing.text.StyledDocument;
import java.util.Set;

/**
 * An observer of changes to properties of a field that are not modeled as addressable HyperTalk properties (and
 * therefore are not observed using the more typical
 * {@link WyldCardPropertiesModel#addPropertyChangedObserver(PropertyChangeObserver)}.
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
