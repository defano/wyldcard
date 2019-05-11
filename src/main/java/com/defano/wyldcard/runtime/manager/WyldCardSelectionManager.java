package com.defano.wyldcard.runtime.manager;

import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.parts.field.AddressableSelection;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.Range;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Singleton;

/**
 * A singleton managing HyperCard's view of the 'the selection'; a special container representing the active text
 * selection.
 */
@Singleton
public class WyldCardSelectionManager implements SelectionManager {

    private PartSpecifier theSelectionPart;     // Part holding 'the selection'
    private Range theSelectionRange;            // Range of characters selected
    private Value theClickText;
    private Value theClickLine;
    private Value theClickChunk;
    private Value theSelectedLoc;

    @Override
    public void setSelection(PartSpecifier selectionPart, Range selectionRange) {
        // Do not allow a message selection from replacing a field selection
        if (!hasFieldSelection() || !(selectionPart != null && selectionPart.getType() == PartType.MESSAGE_BOX)) {
            this.theSelectionPart = selectionPart;
            this.theSelectionRange = selectionRange;
        }
    }

    @Override
    public Range getSelectionRange() {
        return theSelectionRange;
    }

    @Override
    public PartModel getSelectionOwningPart(ExecutionContext context) throws HtException {

        // No selection exists
        if (theSelectionPart == null || getSelectionRange() == null || getSelectionRange().length() == 0) {
            throw new HtSemanticException("There isn't any selection.");
        }

        // Find the part holding the selection
        return context.getPart(theSelectionPart);
    }

    @Override
    public AddressableSelection getManagedSelection(ExecutionContext context) throws HtException {
        PartModel partModel = getSelectionOwningPart(context);

        if (partModel instanceof AddressableSelection) {
            return (AddressableSelection) partModel;
        } else {
            throw new IllegalStateException("Bug! Unexpected part holding selection: " + partModel);
        }
    }

    @Override
    public Value getSelection(ExecutionContext context) throws HtException {
        return getManagedSelection(context).getSelectedText(context);
    }

    @Override
    public void setClickText(Value clickText) {
        theClickText = clickText;
    }

    @Override
    public Value getClickText() {
        return theClickText;
    }

    @Override
    public void setClickChunk(Value clickChunk) {
        this.theClickChunk = clickChunk;
    }

    @Override
    public Value getClickChunk() {
        return this.theClickChunk;
    }

    @Override
    public void setClickLine(Value clickLine) {
        this.theClickLine = clickLine;
    }

    @Override
    public Value getClickLine() {
        return theClickLine;
    }

    @Override
    public Value getSelectedLoc() {
        return this.theSelectedLoc;
    }

    @Override
    public void setSelectedLoc(Value selectedLoc) {
        this.theSelectedLoc = selectedLoc;
    }

    private boolean hasFieldSelection() {
        return theSelectionPart != null &&
                theSelectionPart.getType() == PartType.FIELD &&
                theSelectionRange != null &&
                !theSelectionRange.isEmpty();
    }
}
