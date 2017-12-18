package com.defano.hypercard.runtime.context;

import com.defano.hypercard.parts.field.AddressableSelection;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.Range;

/**
 * A singleton representing the HyperCard state of 'the selection'; a special container representing the active text
 * selection.
 */
public class SelectionContext {

    private final static SelectionContext instance = new SelectionContext();

    private PartSpecifier theSelectionPart;
    private Range theSelectionRange;

    private SelectionContext() {
    }

    public static SelectionContext getInstance() {
        return instance;
    }

    /**
     * Specifies a range of characters in a given part that represents 'the selection'.
     * @param selectionPart The part holding the current selection.
     * @param selectionRange The range of characters in this part that is selected.
     */
    public void setSelection(PartSpecifier selectionPart, Range selectionRange) {
        // Do not allow a message selection from replacing a field selection
        if (!hasFieldSelection() || !(selectionPart != null && selectionPart.getType() == PartType.MESSAGE_BOX)) {
            this.theSelectionPart = selectionPart;
            this.theSelectionRange = selectionRange;
        }
    }

    /**
     * Gets the range of characters (in {@link #getSelectedPart()} that is currently selected.
     * @return The range of selected characters.
     */
    public Range getSelectionRange() {
        return theSelectionRange;
    }

    /**
     * Gets the part currently holding the active selection.
     * @return The model associated with the part holding the active selection.
     * @throws HtSemanticException Thrown if there is no selection.
     */
    public PartModel getSelectedPart() throws HtSemanticException {

        // No selection exists
        if (theSelectionPart == null || getSelectionRange() == null || getSelectionRange().length() == 0) {
            throw new HtSemanticException("There isn't any selection.");
        }

        // Find the part holding the selection
        return ExecutionContext.getContext().getPart(theSelectionPart);
    }

    /**
     * Gets the AddressableSelection object associated with the active selection.
     * @return The AddressableSelection
     * @throws HtSemanticException Thrown if there is no selection.
     */
    public AddressableSelection getManagedSelection() throws HtSemanticException {
        PartModel partModel = getSelectedPart();

        if (partModel instanceof AddressableSelection) {
            return (AddressableSelection) partModel;
        } else {
            throw new IllegalStateException("Bug! Unexpected part holding selection: " + partModel);
        }
    }

    /**
     * Gets the currently selected text.
     * @return The current selection.
     * @throws HtSemanticException Thrown if there is no selection.
     */
    public Value getSelection() throws HtSemanticException {
        return getManagedSelection().getSelectedText();
    }

    private boolean hasFieldSelection() {
        return theSelectionPart != null &&
                theSelectionPart.getType() == PartType.FIELD &&
                theSelectionRange != null &&
                !theSelectionRange.isEmpty();
    }
}
