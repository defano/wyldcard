package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.sound.DefaultSpeechPlaybackManager;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.SpeakingVoice;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.sound.SpeechPlaybackManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class SpeakCmd extends Command {

    @Inject
    private SpeechPlaybackManager speechPlaybackManager;

    private final Expression textExpression;
    private final Expression voiceExpression;

    public SpeakCmd(ParserRuleContext context, Expression text) {
        this(context, text, null);
    }

    public SpeakCmd(ParserRuleContext context, Expression text, Expression voice) {
        super(context, "speak");
        this.textExpression = text;
        this.voiceExpression = voice;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        SpeakingVoice voice = SpeakingVoice.getDefaultVoice();
        if (voiceExpression != null) {
            voice = SpeakingVoice.getVoiceByNameOrGender(voiceExpression.evaluate(context).stringValue());
        }

        String textToSpeak = textExpression.evaluate(context).stringValue();
        speechPlaybackManager.speak(textToSpeak, voice);
    }
}
