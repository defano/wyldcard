package com.defano.wyldcard.sound;

public enum  MusicalDuration {

    WHOLE(1),
    WHOLE_DOTTED(1.5),
    HALF(0.5),
    HALF_DOTTED(0.75),
    QUARTER(0.25),
    QUARTER_DOTTED(0.375),
    EIGHTH(0.125),
    EIGHTH_DOTTED(0.1875),
    SIXTEENTH(0.0625),
    SIXTEENTH_DOTTED(0.09375),
    THIRTY_SECOND(0.03125),
    THIRTY_SECOND_DOTTED(0.046875),
    SIXTY_FOURTH(0.015625),
    SIXTY_FOURTH_DOTTED(0.0234375);

    private final double relativeDuration;

    MusicalDuration(double relativeDuration) {
        this.relativeDuration = relativeDuration;
    }

    public double getRelativeDuration() {
        return this.relativeDuration;
    }

    public double getDurationMs(int forBpm) {
        double SECONDS_PER_MINUTE = 60.0;
        double MILLISECONDS_PER_SECOND = 1000.0;

        return ((SECONDS_PER_MINUTE / forBpm) * MILLISECONDS_PER_SECOND) * getRelativeDuration();
    }

    public MusicalDuration getUndotted() {
        switch (this) {
            case WHOLE_DOTTED: return WHOLE;
            case HALF_DOTTED: return HALF;
            case QUARTER_DOTTED: return QUARTER;
            case EIGHTH_DOTTED: return EIGHTH;
            case SIXTEENTH_DOTTED: return SIXTEENTH;
            case THIRTY_SECOND_DOTTED: return THIRTY_SECOND;
            case SIXTY_FOURTH_DOTTED: return SIXTY_FOURTH;

            default: return this;
        }
    }

    public static MusicalDuration fromString(String duration) {
        if (duration.length() == 0) {
            return MusicalDuration.QUARTER;
        }

        MusicalDuration value;

        switch (duration.charAt(0)) {
            case 'w': value = parseDot(duration) ? WHOLE_DOTTED : WHOLE; break;
            case 'h': value = parseDot(duration) ? HALF_DOTTED : HALF; break;
            case 'q': value = parseDot(duration) ? QUARTER_DOTTED : QUARTER; break;
            case 'e': value = parseDot(duration) ? EIGHTH_DOTTED : EIGHTH; break;
            case 's': value = parseDot(duration) ? SIXTEENTH_DOTTED : SIXTEENTH; break;
            case 't': value = parseDot(duration) ? THIRTY_SECOND_DOTTED : THIRTY_SECOND; break;
            case 'x': value = parseDot(duration) ? SIXTY_FOURTH_DOTTED : SIXTY_FOURTH; break;

            default: value = parseDot(duration) ? QUARTER_DOTTED : QUARTER;
        }

        return value;
    }

    private static boolean parseDot(String duration) {
        return duration.contains(".");
    }
}
