package com.defano.hypercard.gui.sound;

import java.net.URL;

public enum Sound {

    HARPSICHORD("harpsichord", MusicalFrequency.G4, 479, 818),
    BOING("boing", MusicalFrequency.D4),
    FLUTE("flute", MusicalFrequency.A4, 658, 1316);

    private final String resource;
    private final MusicalFrequency dominantFrequency;
    private final Integer loopStart;
    private final Integer loopEnd;

    Sound(String resource, MusicalFrequency dominantFrequency) {
        this(resource, dominantFrequency, null, null);
    }

    Sound(String resource, MusicalFrequency dominantFrequency, Integer loopStart, Integer loopEnd) {
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
        return Sound.class.getResource("/sounds/" + resource + ".wav");
    }

    public MusicalFrequency getDominantFrequency() {
        return dominantFrequency;
    }

    public static Sound fromName(String name) {
        for (Sound thisSound : values()) {
            if (name.equalsIgnoreCase(thisSound.resource)) {
                return thisSound;
            }
        }

        throw new IllegalArgumentException("No such sound named " + name);
    }

}
