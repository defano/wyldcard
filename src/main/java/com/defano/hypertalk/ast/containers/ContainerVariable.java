/*
 * ContainerVariable
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ContainerVariable.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Representation of a variable as a container for Value
 */

package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.Chunk;

public class ContainerVariable extends Container {

    private final String symbol;
    private final Chunk chunk;

    public ContainerVariable(String symbol) {
        this.symbol = symbol;
        this.chunk = null;
    }

    public ContainerVariable(String symbol, Chunk chunk) {
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
        Value value = ExecutionContext.getContext().get(symbol);
        return chunkOf(value, this.chunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {

        Value mutable = ExecutionContext.getContext().get(symbol);

        // Operating on a chunk of the existing value
        if (chunk != null)
            mutable = Value.setChunk(mutable, preposition, chunk, value);
        else
            mutable = Value.setValue(mutable, preposition, value);

        ExecutionContext.getContext().set(symbol, mutable);
        ExecutionContext.getContext().setIt(mutable);
    }

}
