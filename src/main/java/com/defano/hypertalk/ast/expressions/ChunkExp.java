package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ChunkExp extends Expression {

    public final Chunk chunk;
    public final Expression expression;
    
    public ChunkExp(ParserRuleContext context, Chunk chunk, Expression expression) {
        super(context);
        this.chunk = chunk;
        this.expression = expression;
    }
    
    public Value onEvaluate() throws HtException {
        return expression.onEvaluate().getChunk(chunk);
    }
}
