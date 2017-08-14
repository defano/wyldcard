package com.defano.hypercard.gui.sound;

public enum  MusicalDuration {

    WHOLE(1),
    HALF(1/2),
    QUARTER(1/4),
    EIGHTH(1/8),
    SIXTEENTH(1/16),
    THIRTYSECOND(1/32),
    SIXTYFOURTH(1/64);

    private final double relativeDuration;
    private boolean dotted = false;

    MusicalDuration(double relativeDuration) {
        this.relativeDuration = relativeDuration;
    }

    public boolean isDotted() {
        return dotted;
    }

    public void setDotted(boolean dotted) {
        this.dotted = dotted;
    }

    public double getRelativeDuration() {
        return isDotted() ? this.relativeDuration + (this.relativeDuration / 2) : this.relativeDuration;
    }

    public static MusicalDuration of(String duration, MusicalDuration dflt) {
        if (duration.length() == 0) {
            return dflt;
        }

        MusicalDuration value;

        switch (duration.charAt(0)) {
            case 'w': value = WHOLE; break;
            case 'h': value = HALF; break;
            case 'q': value = QUARTER; break;
            case 's': value = SIXTEENTH; break;
            case 't': value = THIRTYSECOND; break;
            case 'x': value = SIXTYFOURTH; break;

            default: return dflt;
        }

        if (duration.length() > 1 && duration.charAt(1) == '.') {
            value.setDotted(true);
        }

        return value;
    }
}
