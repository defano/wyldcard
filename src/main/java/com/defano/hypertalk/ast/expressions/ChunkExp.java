package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.model.chunk.Chunk;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * An expression referring to a chunk of another expression (i.e., 'the first word of x')
 */
public class ChunkExp extends Expression {

    public final Chunk chunk;               // The chunk specification (i.e., 'the first word of')
    public final Expression expression;     // The value whose chunk is being evaluated
    
    public ChunkExp(ParserRuleContext context, Chunk chunk, Expression expression) {
        super(context);
        this.chunk = chunk;
        this.expression = expression;
    }
    
    public Value onEvaluate(ExecutionContext context) throws HtException {
        return expression.onEvaluate(context).getChunk(context, chunk);
    }
}
