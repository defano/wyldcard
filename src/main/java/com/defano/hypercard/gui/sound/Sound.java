package com.defano.hypercard.gui.sound;

import java.net.URL;

public enum Sound {

    HARPSICHORD("harpsichord", MusicalFrequency.G4),
    BOING("boing", MusicalFrequency.D4),
    FLUTE("flute", MusicalFrequency.A4);

    private final String resource;
    private final MusicalFrequency dominantFrequency;

    Sound(String resource, MusicalFrequency dominantFrequency) {
        this.resource = resource;
        this.dominantFrequency = dominantFrequency;
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
