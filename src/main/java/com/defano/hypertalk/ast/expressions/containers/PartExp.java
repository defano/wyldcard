package com.defano.hypertalk.ast.expressions.containers;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;


public abstract class PartExp extends ContainerExp {

    public abstract PartSpecifier evaluateAsSpecifier() throws HtException;

    public PartExp(ParserRuleContext context) {
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
