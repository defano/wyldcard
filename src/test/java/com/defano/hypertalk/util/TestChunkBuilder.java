package com.defano.hypertalk.util;

import com.defano.hypertalk.ast.expression.LiteralExp;
import com.defano.hypertalk.ast.model.chunk.Chunk;
import com.defano.hypertalk.ast.model.chunk.ChunkType;
import com.defano.hypertalk.ast.model.Value;

public class TestChunkBuilder {

    public static Chunk buildSingleChunk(ChunkType type, int item) {
        return new Chunk(type, new LiteralExp(null, new Value(item)));
    }

    public static Chunk buildChunkRange(ChunkType type, int start, int end) {
        return new Chunk(type, new LiteralExp(null, new Value(start)), new LiteralExp(null, new Value(end)));
    }

}
