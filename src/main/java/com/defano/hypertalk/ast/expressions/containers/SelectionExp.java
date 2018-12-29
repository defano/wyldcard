package com.defano.hypertalk.ast.expressions.containers;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.field.AddressableSelection;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.utils.Range;
import org.antlr.v4.runtime.ParserRuleContext;

public class SelectionExp extends ContainerExp {

    public SelectionExp(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        return chunkOf(context, WyldCard.getInstance().getSelectionManager().getSelection(context), getChunk());
    }

    @Override
    public void putValue(ExecutionContext context, Value value, Preposition preposition) throws HtException {

        Value oldSelection = WyldCard.getInstance().getSelectionManager().getSelection(context);
        Range range = WyldCard.getInstance().getSelectionManager().getSelectionRange();
        AddressableSelection field = WyldCard.getInstance().getSelectionManager().getManagedSelection(context);
        PartModel partModel = WyldCard.getInstance().getSelectionManager().getSelectedPart(context);

        // Create the new selectedText
        Value newSelection;
        if (getChunk() != null)
            newSelection = Value.ofMutatedChunk(context, oldSelection, preposition, getChunk(), value);
        else
            newSelection = Value.ofValue(oldSelection, preposition, value);

        // Replace the current selection with the new selection
        partModel.setValue(Value.ofMutatedChunk(context, partModel.getValue(context), Preposition.INTO, range.asChunk(), newSelection), context);

        // Select the new range of text in the destination
        int newSelectionLength = newSelection.toString().length();
        ThreadUtils.invokeAndWaitAsNeeded(() -> field.setSelection(context, range.start, range.start + newSelectionLength));
    }

}
