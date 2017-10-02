package com.defano.hypercard.runtime.context;

import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypercard.parts.field.ManagedSelection;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.msgbox.MsgBoxModel;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartSpecifier;
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
        if (!hasFieldSelection() || !(selectionPart != null && selectionPart.type() == PartType.MESSAGE_BOX)) {
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
        return ExecutionContext.getContext().getCurrentCard().findPart(theSelectionPart);
    }

    public ManagedSelection getManagedSelection() throws HtSemanticException {
        PartModel partModel = getSelectedPart();

        if (partModel instanceof FieldModel) {
            return  (ManagedSelection) ExecutionContext.getContext().getCurrentCard().getPart(partModel);
        } else if (partModel instanceof MsgBoxModel) {
            return WindowManager.getMessageWindow();
        } else {
            throw new IllegalStateException("Bug! Unexpected part holding selection: " + partModel);
        }
    }

    public Value getSelection() throws HtSemanticException {
        return getManagedSelection().getSelectedText();
    }

    private boolean hasFieldSelection() {
        return theSelectionPart != null &&
                theSelectionPart.type() == PartType.FIELD &&
                theSelectionRange != null &&
                !theSelectionRange.isEmpty();
    }
}
