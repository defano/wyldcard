package com.defano.hypertalk.ast.expression.container;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.part.field.AddressableSelection;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import com.defano.hypertalk.ast.model.enums.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.util.Range;
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
        PartModel partModel = WyldCard.getInstance().getSelectionManager().getSelectionOwningPart(context);

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
        Invoke.onDispatch(() -> field.setSelection(context, range.start, range.start + newSelectionLength));
    }

}
