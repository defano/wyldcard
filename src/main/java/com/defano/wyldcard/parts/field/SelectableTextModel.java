package com.defano.wyldcard.parts.field;

import com.defano.hypertalk.utils.Range;
import com.defano.wyldcard.runtime.context.ExecutionContext;

/**
 * Represents a model that supports programmatic interrogation and modification of a selection of text.
 */
public interface SelectableTextModel {

    /**
     * Gets the plaintext representation of the selectable text.
     * @return The selectable text.
     * @param context The execution context.
     */
    String getText(ExecutionContext context);

    /**
     * Sets the current text selection to the given range of characters. No selection is made is if the length of
     * the range is zero.
     *
     * @param context The execution context.
     * @param selection The new selection.
     */
    void setSelection(ExecutionContext context, Range selection);

    /**
     * Gets the current text selection; returns a zero-length range if no text is currently selected. Certain selections
     * apply only to the part (like auto-selections), in which case they do not count as "HyperCard's" selection.
     *
     * @return The current text selection.
     * @param context The execution context.
     */
    Range getSelection(ExecutionContext context);

    /**
     * Invoked to indicate that the view component has updated the selection.
     *
     * This method is used to notify the model of selection changes that should not generate a corresponding
     * notification for the view to update itself.
     *
     * @param selection The new selection as provided by the view.
     */
    void onViewDidUpdateSelection(Range selection);
}
