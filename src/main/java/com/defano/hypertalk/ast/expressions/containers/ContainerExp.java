package com.defano.hypertalk.ast.expressions.containers;

import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.model.chunk.Chunk;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Represents an assignable expression (an "l-value" in C parlance); that is, an expression that a value can be placed
 * in to.
 */
public abstract class ContainerExp extends Expression {

    private Chunk chunk;

    public ContainerExp(ParserRuleContext context) {
        super(context);
    }

    public abstract void putValue(ExecutionContext context, Value value, Preposition preposition) throws HtException;

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

    protected Value chunkOf(ExecutionContext context, Value v, Chunk chunk) throws HtException {
        if (chunk == null) {
            return v;
        } else {
            return v.getChunk(context, chunk);
        }
    }
}