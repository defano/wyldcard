package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class VisualEffectExp extends Expression {

    public final VisualEffectSpecifier effectSpecifier;

    public VisualEffectExp(ParserRuleContext context, VisualEffectSpecifier effectSpecifier) {
        super(context);
        this.effectSpecifier = effectSpecifier;
    }

    @Override
    protected Value onEvaluate() throws HtException {
        return new Value(getContext().getText());
    }

}
