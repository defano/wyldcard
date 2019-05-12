package com.defano.hypertalk.ast.expression.function;

import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.sound.SpeechPlaybackManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class SpeechFunc extends Expression {

    @Inject
    private SpeechPlaybackManager speechPlaybackManager;

    public SpeechFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) {
        return speechPlaybackManager.getTheSpeech();
    }
}
