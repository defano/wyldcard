package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.exception.HtException;


public class PartContainer extends Container {

    private final PartExp part;

    public PartContainer(PartExp part) {
        this.part = part;
    }

    public PartExp part() {
        return part;
    }

    @Override
    public Value getValue() throws HtException {
        Value value = ExecutionContext.getContext().getPart(part.evaluateAsSpecifier()).getValue();
        return chunkOf(value, getChunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        Value destValue = ExecutionContext.getContext().getPart(part.evaluateAsSpecifier()).getValue();

        // Operating on a chunk of the existing value
        if (getChunk() != null)
            destValue = Value.setChunk(destValue, preposition, getChunk(), value);
        else
            destValue = Value.setValue(destValue, preposition, value);

        ExecutionContext.getContext().getPart(part.evaluateAsSpecifier()).setValue(destValue);
        ExecutionContext.getContext().setIt(destValue);
    }
}
