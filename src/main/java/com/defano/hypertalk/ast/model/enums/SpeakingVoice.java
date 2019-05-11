package com.defano.hypertalk.ast.model.enums;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum SpeakingVoice {

    PRUDENCE("dfki-prudence-hsmm", "female"),
    MARY("cmu-slt-hsmm", "female"),
    BILL("cmu-bdl-hsmm", "male"),
    DAN("cmu-rms-hsmm", "male");

    private String voiceId;
    private String gender;

    SpeakingVoice(String voiceId, String gender) {
        this.voiceId = voiceId;
        this.gender = gender;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public String getGender() {
        return gender;
    }

    public static SpeakingVoice getDefaultVoice() {
        return values()[0];     // First enumerated voice is the default
    }

    public static SpeakingVoice getVoiceByGender(String gender) {
        for (SpeakingVoice voice : values()) {
            if (voice.gender.equalsIgnoreCase(gender)) {
                return voice;
            }
        }

        return null;
    }

    public static SpeakingVoice getVoiceByName(String name) {
        for (SpeakingVoice voice : values()) {
            if (voice.name().equalsIgnoreCase(name)) {
                return voice;
            }
        }

        return null;
    }

    public static SpeakingVoice getVoiceByNameOrGender(String nameOrGender) {
        SpeakingVoice byGender = getVoiceByGender(nameOrGender);
        if (byGender != null) {
            return byGender;
        }

        SpeakingVoice byName = getVoiceByName(nameOrGender);
        if (byName != null) {
            return byName;
        }

        return getDefaultVoice();
    }

    public static List<Value> getVoices() {
        ArrayList<Value> voices = new ArrayList<>();
        for (SpeakingVoice thisVoice : values()) {
            voices.add(new Value(StringUtils.capitalize(thisVoice.name())));
        }
        return voices;
    }

}
