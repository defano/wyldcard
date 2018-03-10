package com.defano.hypertalk.ast.expressions.functions;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class TargetFunc extends Expression {

    public TargetFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate() throws HtException {
        return new Value(ExecutionContext.getContext().getTarget().getHyperTalkIdentifier());
    }
}
