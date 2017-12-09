package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;


public abstract class PartContainerExp extends ContainerExp {

    public abstract PartSpecifier evaluateAsSpecifier() throws HtException;

    public PartContainerExp(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate() throws HtException {
        Value value = ExecutionContext.getContext().getPart(evaluateAsSpecifier()).getValue();
        return chunkOf(value, getChunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        Value destValue = ExecutionContext.getContext().getPart(evaluateAsSpecifier()).getValue();

        // Operating on a chunk of the existing value
        if (getChunk() != null)
            destValue = Value.setChunk(destValue, preposition, getChunk(), value);
        else
            destValue = Value.setValue(destValue, preposition, value);

        ExecutionContext.getContext().getPart(evaluateAsSpecifier()).setValue(destValue);
        ExecutionContext.getContext().setIt(destValue);
    }
}
