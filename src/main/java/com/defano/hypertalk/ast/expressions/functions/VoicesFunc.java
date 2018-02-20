package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.SpeakingVoice;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class VoicesFunc extends Expression {

    public VoicesFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate() throws HtException {
        return Value.ofItems(SpeakingVoice.getVoices());
    }
}
