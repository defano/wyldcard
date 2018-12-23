package com.defano.wyldcard.runtime.context;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.Range;
import com.defano.wyldcard.parts.field.AddressableSelection;
import com.defano.wyldcard.parts.model.PartModel;

public interface SelectionManager {
    /**
     * Specifies a range of characters in a given part that represents 'the selection'.
     * @param selectionPart The part holding the current selection.
     * @param selectionRange The range of characters in this part that is selected.
     */
    void setSelection(PartSpecifier selectionPart, Range selectionRange);

    /**
     * Gets the range of characters (in {@link #getSelectedPart(ExecutionContext)} that is currently selected.
     * @return The range of selected characters.
     */
    Range getSelectionRange();

    /**
     * Gets the part currently holding the active selection.
     * @return The model associated with the part holding the active selection.
     * @throws HtSemanticException Thrown if there is no selection.
     * @param context The execution context.
     */
    PartModel getSelectedPart(ExecutionContext context) throws HtSemanticException;

    /**
     * Gets the AddressableSelection object associated with the active selection.
     * @return The AddressableSelection
     * @throws HtSemanticException Thrown if there is no selection.
     * @param context The execution context.
     */
    AddressableSelection getManagedSelection(ExecutionContext context) throws HtSemanticException;

    /**
     * Gets the currently selected text.
     * @return The current selection.
     * @throws HtSemanticException Thrown if there is no selection.
     * @param context The execution context.
     */
    Value getSelection(ExecutionContext context) throws HtSemanticException;
}
