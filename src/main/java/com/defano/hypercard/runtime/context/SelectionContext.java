package com.defano.hypercard.runtime.context;

import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.containers.PartSpecifier;
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

        // Do not allow an empty message selection to clear an existing field selection
        if (!hasFieldSelection() || !isEmptyMessageSelection(selectionPart, selectionRange)) {
            this.theSelectionPart = selectionPart;
            this.theSelectionRange = selectionRange;
        }
    }

    public PartSpecifier getSelectionPartSpecifier() {
        return theSelectionPart;
    }

    public Range getSelectionRange() {
        return theSelectionRange;
    }

    private boolean isEmptyMessageSelection(PartSpecifier selectionPart, Range selectionRange) {
        return selectionPart != null && selectionPart.type() == PartType.MESSAGE_BOX && selectionRange.isEmpty();
    }

    private boolean hasFieldSelection() {
        return theSelectionPart != null &&
                theSelectionPart.type() == PartType.FIELD &&
                theSelectionRange != null &&
                !theSelectionRange.isEmpty();
    }
}
