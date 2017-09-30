package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.parts.field.FieldPart;
import com.defano.hypercard.parts.field.styles.AbstractTextField;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.runtime.context.HyperCardProperties;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.Range;

public class ContainerSelection extends Container {

    private final Chunk chunk;

    public ContainerSelection(Chunk chunk) {
        this.chunk = chunk;
    }

    public ContainerSelection() {
        this.chunk = null;
    }

    @Override
    public Value getValue() throws HtException {
        Value value = HyperCardProperties.getInstance().getKnownProperty(HyperCardProperties.PROP_SELECTEDTEXT);
        return chunkOf(value, chunk);
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {

        // Get the text, part, and range of the active selection
        Value oldSelection = HyperCardProperties.getInstance().getKnownProperty(HyperCardProperties.PROP_SELECTEDTEXT);
        PartSpecifier partSpecifier = HyperCardProperties.getInstance().getSelectionPartSpecifier();
        Range range = HyperCardProperties.getInstance().getSelectionRange();

        // No selection exists
        if (partSpecifier == null || range == null || range.length() == 0) {
            throw new HtSemanticException("There isn't any selection.");
        }

        // Create the new selectedText
        Value newSelection;
        if (chunk != null)
            newSelection = Value.setChunk(oldSelection, preposition, chunk, value);
        else
            newSelection = Value.setValue(oldSelection, preposition, value);

        // Find the part holding the selection
        PartModel partModel = ExecutionContext.getContext().getCurrentCard().findPart(partSpecifier);
        FieldPart field = (FieldPart) ExecutionContext.getContext().getCurrentCard().getPart(partModel);

        // Replace the current selection with the new selection
        partModel.setValue(Value.setChunk(partModel.getValue(), Preposition.INTO, range.asChunk(), newSelection));

        // Select the new range of text in the destination
        int newSelectionLength = newSelection.toString().length();
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            ((AbstractTextField)field.getComponent()).setSelection(range.start, range.start + newSelectionLength);
        });
    }

}
