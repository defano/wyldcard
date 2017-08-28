package com.defano.hypercard.gui.sound;

public enum  MusicalDuration {

    WHOLE(1),
    HALF(1.0/2.0),
    QUARTER(1.0/4.0),
    EIGHTH(1.0/8.0),
    SIXTEENTH(1.0/16.0),
    THIRTYSECOND(1.0/32.0),
    SIXTYFOURTH(1.0/64.0);

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

    public double getDurationMs(int forBpm) {
        double SECONDS_PER_MINUTE = 60.0;
        double MILLISECONDS_PER_SECOND = 1000.0;

        return ((SECONDS_PER_MINUTE / forBpm) * MILLISECONDS_PER_SECOND) * getRelativeDuration();
    }

    public static MusicalDuration fromString(String duration) {
        if (duration.length() == 0) {
            return MusicalDuration.QUARTER;
        }

        MusicalDuration value;

        switch (duration.charAt(0)) {
            case 'w': value = WHOLE; break;
            case 'h': value = HALF; break;
            case 'q': value = QUARTER; break;
            case 'e': value = EIGHTH; break;
            case 's': value = SIXTEENTH; break;
            case 't': value = THIRTYSECOND; break;
            case 'x': value = SIXTYFOURTH; break;

            default: return MusicalDuration.QUARTER;
        }

        if (duration.contains(".")) {
            value.setDotted(true);
        }

        return value;
    }

    @Override
    public String toString() {
        return "MusicalDuration{" +
                "relativeDuration=" + relativeDuration +
                ", dotted=" + dotted +
                '}';
    }
}
