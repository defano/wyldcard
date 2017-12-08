package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class VariableContainerExp extends ContainerExp {

    private final String symbol;

    public VariableContainerExp(ParserRuleContext context, String symbol) {
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
