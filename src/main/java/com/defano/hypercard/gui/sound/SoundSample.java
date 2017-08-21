package com.defano.hypercard.gui.sound;

import java.net.URL;

public enum SoundSample {

    HARPSICHORD("harpsichord", MusicalPitch.G4),
    BOING("boing", MusicalPitch.D4),
    FLUTE("flute", MusicalPitch.A3, 658, 1316),
    SILENCE("silence", MusicalPitch.REST),
    DIAL_0("dial0", MusicalPitch.C4),
    DIAL_1("dial1", MusicalPitch.C4),
    DIAL_2("dial2", MusicalPitch.C4),
    DIAL_3("dial3", MusicalPitch.C4),
    DIAL_4("dial4", MusicalPitch.C4),
    DIAL_5("dial5", MusicalPitch.C4),
    DIAL_6("dial6", MusicalPitch.C4),
    DIAL_7("dial7", MusicalPitch.C4),
    DIAL_8("dial8", MusicalPitch.C4),
    DIAL_9("dial9", MusicalPitch.C4),
    DIAL_STAR("dial*", MusicalPitch.C4),
    DIAL_HASH("dial#", MusicalPitch.C4);

    private final String resource;
    private final MusicalPitch dominantFrequency;
    private final Integer loopStart;
    private final Integer loopEnd;

    SoundSample(String resource, MusicalPitch dominantFrequency) {
        this(resource, dominantFrequency, null, null);
    }

    SoundSample(String resource, MusicalPitch dominantFrequency, Integer loopStart, Integer loopEnd) {
        this.resource = resource;
        this.dominantFrequency = dominantFrequency;
        this.loopStart = loopStart;
        this.loopEnd = loopEnd;
    }

    public boolean isStretchable() {
        return loopEnd != null && loopStart != null;
    }

    public int getLoopStart() {
        return loopStart;
    }

    public int getLoopEnd() {
        return loopEnd;
    }

    public URL getResource() {
        return SoundSample.class.getResource("/sounds/" + resource + ".wav");
    }

    public MusicalPitch getDominantFrequency() {
        return dominantFrequency;
    }

    public static SoundSample fromName(String name) {
        for (SoundSample thisSoundSample : values()) {
            if (name.equalsIgnoreCase(thisSoundSample.resource)) {
                return thisSoundSample;
            }
        }

        throw new IllegalArgumentException("No such sound named " + name);
    }

}
