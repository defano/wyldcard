package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.Chunk;

public class VariableContainer extends Container {

    private final String symbol;

    public VariableContainer(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public Value getValue() throws HtException {
        Value value = ExecutionContext.getContext().getVariable(symbol);
        return chunkOf(value, getChunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        ExecutionContext.getContext().setVariable(symbol, preposition, getChunk(), value);
    }

}
