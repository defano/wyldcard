package com.defano.hypercard.runtime.context;

import com.defano.hypercard.parts.field.AddressableSelection;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.Range;

public class SelectionContext {

    private final static SelectionContext instance = new SelectionContext();

    private PartSpecifier theSelectionPart;
    private Range theSelectionRange;

    private SelectionContext() {
    }

    public static SelectionContext getInstance() {
        return instance;
    }

    public void setTheSelection(PartSpecifier selectionPart, Range selectionRange) {
        // Do not allow a message selection from replacing a field selection
        if (!hasFieldSelection() || !(selectionPart != null && selectionPart.getType() == PartType.MESSAGE_BOX)) {
            this.theSelectionPart = selectionPart;
            this.theSelectionRange = selectionRange;
        }
    }

    public Range getSelectionRange() {
        return theSelectionRange;
    }

    public PartModel getSelectedPart() throws HtSemanticException {

        // No selection exists
        if (theSelectionPart == null || getSelectionRange() == null || getSelectionRange().length() == 0) {
            throw new HtSemanticException("There isn't any selection.");
        }

        // Find the part holding the selection
        return ExecutionContext.getContext().getCurrentCard().getCardModel().findPart(theSelectionPart);
    }

    public AddressableSelection getManagedSelection() throws HtSemanticException {
        PartModel partModel = getSelectedPart();

        if (partModel instanceof AddressableSelection) {
            return (AddressableSelection) partModel;
        } else {
            throw new IllegalStateException("Bug! Unexpected part holding selection: " + partModel);
        }
    }

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
