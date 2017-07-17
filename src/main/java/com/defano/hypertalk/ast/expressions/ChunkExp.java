/*
 * ExpChunk
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ChunkExp.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a chunk expression in HyperTalk, for example: "the second word of..."
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;

public class ChunkExp extends Expression {

    public final Chunk chunk;
    public final Expression expression;
    
    public ChunkExp(Chunk chunk, Expression expression) {
        this.chunk = chunk;
        this.expression = expression;
    }
    
    public Value evaluate () throws HtSemanticException {
        return expression.evaluate().getChunk(chunk);
    }
}
