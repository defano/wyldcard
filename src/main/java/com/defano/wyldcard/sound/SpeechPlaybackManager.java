package com.defano.wyldcard.sound;

import com.defano.hypertalk.ast.model.SpeakingVoice;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;

public interface SpeechPlaybackManager {
    Value getTheSpeech();

    void speak(String text, SpeakingVoice voice) throws HtException;
}
