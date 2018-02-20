package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypercard.sound.SpeechPlaybackExecutor;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class SpeechFunc extends Expression {

    public SpeechFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate() throws HtException {
        return SpeechPlaybackExecutor.getInstance().getTheSpeech();
    }
}
