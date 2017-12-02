package com.defano.hypertalk.ast.containers;

import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.Chunk;

public abstract class Container {

    private Chunk chunk;

    public abstract Value getValue() throws HtException;
    public abstract void putValue(Value value, Preposition preposition) throws HtException;

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

    protected Value chunkOf (Value v, Chunk chunk) throws HtException {
        if (chunk == null) {
            return v;
        } else {
            return v.getChunk(chunk);
        }
    }
}

