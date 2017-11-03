package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class DestinationExp extends PartExp {

    public DestinationExp(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate() throws HtSemanticException {
        throw new IllegalStateException("Destinations are not evaluable.");
    }
}
