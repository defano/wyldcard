package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.effect.VisualEffectDirection;
import com.defano.hypertalk.ast.model.effect.VisualEffectImage;
import com.defano.hypertalk.ast.model.effect.VisualEffectName;
import com.defano.hypertalk.ast.model.effect.VisualEffectSpeed;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.jsegue.SegueName;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class VisualEffectExp extends Expression {

    private final Expression effectNameExp;
    private final Expression effectDirectionExp;
    private final Expression effectSpeedExp;
    private final Expression effectImageExp;

    public VisualEffectExp(ParserRuleContext context, Expression effectNameExp, Expression effectDirectionExp, Expression effectSpeedExp, Expression effectImageExp) {
        super(context);
        this.effectNameExp = effectNameExp;
        this.effectDirectionExp = effectDirectionExp;
        this.effectSpeedExp = effectSpeedExp;
        this.effectImageExp = effectImageExp;
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        context.getCurrentStack().getCurtainManager().lockScreen(context);
        context.setVisualEffect(evaluateAsSpecifier(context));
        return new Value();
    }

    public VisualEffectSpecifier evaluateAsSpecifier(ExecutionContext context) throws HtException {
        VisualEffectSpeed speed = VisualEffectSpeed.FAST;
        VisualEffectImage image = VisualEffectImage.CARD;

        VisualEffectName name = VisualEffectName.fromHypertalkName(effectNameExp.evaluate(context).toString());
        VisualEffectDirection direction = VisualEffectDirection.fromHypertalkName(effectDirectionExp.evaluate(context).toString());
        SegueName segueName = name.toSegueName(direction);

        if (effectSpeedExp != null) {
            speed = VisualEffectSpeed.fromHypertalkName(effectSpeedExp.evaluate(context).toString());
        }

        if (effectImageExp != null) {
            image = VisualEffectImage.fromHypertalkName(effectImageExp.evaluate(context).toString());
        }

        return new VisualEffectSpecifier(segueName, speed, image);
    }

}
