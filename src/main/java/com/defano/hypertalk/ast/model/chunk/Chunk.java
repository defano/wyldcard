package com.defano.hypertalk.ast.model.chunk;

import com.defano.hypertalk.ast.expression.Expression;

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
