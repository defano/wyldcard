/*
 * ContainerProperty
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class ContainerProperty extends Container {

    public final PropertySpecifier propertySpec;
    public final Chunk chunk;

    public ContainerProperty(PropertySpecifier propertySpec) {
        this.propertySpec = propertySpec;
        this.chunk = null;
    }

    public ContainerProperty(PropertySpecifier propertySpec, Chunk chunk) {
        this.propertySpec = propertySpec;
        this.chunk = chunk;
    }

    public Chunk chunk() {
        return chunk;
    }

    @Override
    public Value getValue() throws HtException {
        Value propertyValue = ExecutionContext.getContext().get(getPartSpecifier()).getProperty(getPropertyName());
        return chunkOf(propertyValue, this.chunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        ExecutionContext.getContext().set(propertySpec.property, propertySpec.partExp.evaluateAsSpecifier(), preposition, chunk, value);
    }

    public PartSpecifier getPartSpecifier() throws HtSemanticException {
        return propertySpec.partExp.evaluateAsSpecifier();
    }

    public String getPropertyName() {
        return propertySpec.property;
    }
}
