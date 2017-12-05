package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.parts.field.AddressableSelection;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.SelectionContext;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.utils.Range;

public class SelectionContainer extends Container {

    @Override
    public Value getValue() throws HtException {
        return chunkOf(SelectionContext.getInstance().getSelection(), getChunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {

        Value oldSelection = SelectionContext.getInstance().getSelection();
        Range range = SelectionContext.getInstance().getSelectionRange();
        AddressableSelection field = SelectionContext.getInstance().getManagedSelection();
        PartModel partModel = SelectionContext.getInstance().getSelectedPart();

        // Create the new selectedText
        Value newSelection;
        if (getChunk() != null)
            newSelection = Value.setChunk(oldSelection, preposition, getChunk(), value);
        else
            newSelection = Value.setValue(oldSelection, preposition, value);

        // Replace the current selection with the new selection
        partModel.setValue(Value.setChunk(partModel.getValue(), Preposition.INTO, range.asChunk(), newSelection));

        // Select the new range of text in the destination
        int newSelectionLength = newSelection.toString().length();
        ThreadUtils.invokeAndWaitAsNeeded(() -> field.setSelection(range.start, range.start + newSelectionLength));
    }

}
