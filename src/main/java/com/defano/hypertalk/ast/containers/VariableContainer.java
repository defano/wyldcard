package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.Chunk;

public class VariableContainer extends Container {

    private final String symbol;
    private final Chunk chunk;

    public VariableContainer(String symbol) {
        this.symbol = symbol;
        this.chunk = null;
    }

    public VariableContainer(String symbol, Chunk chunk) {
        this.symbol = symbol;
        this.chunk = chunk;
    }

    public String symbol() {
        return symbol;
    }

    public Chunk chunk() {
        return chunk;
    }

    @Override
    public Value getValue() throws HtException {
        Value value = ExecutionContext.getContext().getVariable(symbol);
        return chunkOf(value, this.chunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {

        Value mutable = ExecutionContext.getContext().getVariable(symbol);

        // Operating on a chunk of the existing value
        if (chunk != null)
            mutable = Value.setChunk(mutable, preposition, chunk, value);
        else
            mutable = Value.setValue(mutable, preposition, value);

        ExecutionContext.getContext().setVariable(symbol, mutable);
        ExecutionContext.getContext().setIt(mutable);
    }

}
