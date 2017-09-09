/*
 * ContainerPart
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ContainerPart.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Representation of a HyperCard part as a container for Value
 */

package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.Chunk;


public class ContainerPart extends Container {

    private final PartExp part;
    private final Chunk chunk;

    public ContainerPart(PartExp part) {
        this.part = part;
        this.chunk = null;
    }

    public ContainerPart(PartExp part, Chunk chunk) {
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
        Value destValue = getValue();

        // Operating on a chunk of the existing value
        if (chunk != null)
            destValue = Value.setChunk(destValue, preposition, chunk, value);
        else
            destValue = Value.setValue(destValue, preposition, value);

        ExecutionContext.getContext().get(part.evaluateAsSpecifier()).setValue(destValue);
        ExecutionContext.getContext().setIt(destValue);
    }
}
