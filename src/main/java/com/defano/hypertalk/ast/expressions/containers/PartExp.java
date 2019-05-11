package com.defano.hypertalk.ast.expressions.containers;

import com.defano.hypertalk.ast.model.enums.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;


public abstract class PartExp extends ContainerExp {

    public abstract PartSpecifier evaluateAsSpecifier(ExecutionContext context) throws HtException;

    public PartExp(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        Value value = context.getPart(evaluateAsSpecifier(context)).getValue(context);
        return chunkOf(context, value, getChunk());
    }

    @Override
    public void putValue(ExecutionContext context, Value value, Preposition preposition) throws HtException {
        Value destValue = context.getPart(evaluateAsSpecifier(context)).getValue(context);

        // Operating on a chunk of the existing value
        if (getChunk() != null)
            destValue = Value.ofMutatedChunk(context, destValue, preposition, getChunk(), value);
        else
            destValue = Value.ofValue(destValue, preposition, value);

        context.getPart(evaluateAsSpecifier(context)).setValue(destValue, context);
        context.setIt(destValue);
    }
}
