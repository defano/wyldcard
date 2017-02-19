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

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.expressions.ExpPart;


public class ContainerPart extends Container {

    private final ExpPart part;
    private final Chunk chunk;

    public ContainerPart(ExpPart part) {
        this.part = part;
        this.chunk = null;
    }

    public ContainerPart(ExpPart part, Chunk chunk) {
        this.part = part;
        this.chunk = chunk;
    }

    public ExpPart part() {
        return part;
    }

    public Chunk chunk() {
        return chunk;
    }

    @Override
    public Value getValue() throws HtException {
        Value value = GlobalContext.getContext().get(part.evaluateAsSpecifier()).getValue();
        return chunkOf(value, this.chunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        GlobalContext.getContext().put(value, preposition, this);
    }
}
