package com.defano.hypertalk.ast.expressions.containers;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class VariableExp extends ContainerExp {

    private final String symbol;

    public VariableExp(ParserRuleContext context, String symbol) {
        super(context);
        this.symbol = symbol;
    }

    @Override
    public Value onEvaluate() throws HtException {
        Value value = ExecutionContext.getContext().getVariable(symbol);
        return chunkOf(value, getChunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        ExecutionContext.getContext().setVariable(symbol, preposition, getChunk(), value);
    }

}
