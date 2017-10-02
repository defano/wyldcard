/*
 * Container
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * Container.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Abstract superclass of any HyperTalk element capable of accepting a value.
 * In this context, "container" is analagous to "l-value"
 */

package com.defano.hypertalk.ast.containers;

import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.exception.HtSemanticException;

public abstract class Container {

    public abstract Value getValue() throws HtException;
    public abstract void putValue(Value value, Preposition preposition) throws HtException;

    protected Value chunkOf (Value v, Chunk chunk) throws HtSemanticException {
        if (chunk == null) {
            return v;
        } else {
            return v.getChunk(chunk);
        }
    }
}

