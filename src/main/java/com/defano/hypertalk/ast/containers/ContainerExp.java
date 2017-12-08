package com.defano.hypertalk.ast.containers;

import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.Chunk;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class ContainerExp extends Expression {

    private Chunk chunk;

    public ContainerExp(ParserRuleContext context) {
        super(context);
    }

    public abstract void putValue(Value value, Preposition preposition) throws HtException;

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

    protected Value chunkOf (Value v, Chunk chunk) throws HtException {
        if (chunk == null) {
            return v;
        } else {
            return v.getChunk(chunk);
        }
    }
}

