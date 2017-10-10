package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.Chunk;


public class PartContainer extends Container {

    private final PartExp part;
    private final Chunk chunk;

    public PartContainer(PartExp part) {
        this.part = part;
        this.chunk = null;
    }

    public PartContainer(PartExp part, Chunk chunk) {
        this.part = part;
        this.chunk = chunk;
    }

    public PartExp part() {
        return part;
    }

    public Chunk chunk() {
        return chunk;
    }

    @Override
    public Value getValue() throws HtException {
        Value value = ExecutionContext.getContext().get(part.evaluateAsSpecifier()).getValue();
        return chunkOf(value, this.chunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        Value destValue = ExecutionContext.getContext().get(part.evaluateAsSpecifier()).getValue();

        // Operating on a chunk of the existing value
        if (chunk != null)
            destValue = Value.setChunk(destValue, preposition, chunk, value);
        else
            destValue = Value.setValue(destValue, preposition, value);

        ExecutionContext.getContext().get(part.evaluateAsSpecifier()).setValue(destValue);
        ExecutionContext.getContext().setIt(destValue);
    }
}
