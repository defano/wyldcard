/*
 * Chunk
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * Chunk.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a chunked expression.
 */

package com.defano.hypertalk.ast.common;

import com.defano.hypertalk.ast.expressions.Expression;

public class Chunk {

    public final ChunkType type;
    public final Expression start;
    public final Expression end;
    
    public Chunk (ChunkType type, Expression item) {
        this.type = type;
        this.start = item;
        this.end = null;
    }
    
    public Chunk (ChunkType type, Expression start, Expression end) {
        this.type = type;
        this.start = start;
        this.end = end;
    }

}
