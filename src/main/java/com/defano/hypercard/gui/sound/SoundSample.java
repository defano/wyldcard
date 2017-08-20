package com.defano.hypercard.gui.sound;

import java.net.URL;

public enum SoundSample {

    HARPSICHORD("harpsichord", MusicalPitch.G4),
    BOING("boing", MusicalPitch.D4),
    FLUTE("flute", MusicalPitch.A3, 658, 1316),
    SILENCE("silence", MusicalPitch.REST);

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
