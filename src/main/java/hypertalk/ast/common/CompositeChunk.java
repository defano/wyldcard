package hypertalk.ast.common;

import hypertalk.ast.expressions.Expression;

public class CompositeChunk extends Chunk {

    public final Chunk chunkOf;

    public CompositeChunk(ChunkType type, Expression start, Expression end, Chunk chunkOf) {
        super(type, start, end);
        this.chunkOf = chunkOf;
    }
}
