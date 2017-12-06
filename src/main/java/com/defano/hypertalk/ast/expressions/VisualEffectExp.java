package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.CompilationUnit;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class VisualEffectExp extends Expression {

    private final Expression expression;
    private final VisualEffectSpecifier specifier;

    public VisualEffectExp(ParserRuleContext context, Expression expression) {
        super(context);

        this.expression = expression;
        this.specifier = null;
    }

    public VisualEffectExp(ParserRuleContext context, VisualEffectSpecifier specifier) {
        super(context);

        this.expression = null;
        this.specifier = specifier;
    }

    @Override
    protected Value onEvaluate() throws HtException {
        return new Value(getContext().getText());
    }

    public VisualEffectSpecifier evaluateAsVisualEffect() throws HtException {
        if (specifier != null) {
            return specifier;
        } else {
            VisualEffectExp exp = Interpreter.evaluate(CompilationUnit.EFFECT_EXPRESSION, expression.evaluate(), VisualEffectExp.class);
            return exp == null ? null : exp.evaluateAsVisualEffect();
        }
    }
}
