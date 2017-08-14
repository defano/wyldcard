package com.defano.hypercard.gui.sound;

public class MusicalNote {

    private final MusicalFrequency frequency;
    private final MusicalDuration duration;

    public MusicalNote (MusicalFrequency frequency, MusicalDuration duration) {
        this.frequency = frequency;
        this.duration = duration;
    }

    public static MusicalNote ofMiddleCQuarterNote() {
        return new MusicalNote(MusicalFrequency.C4, MusicalDuration.QUARTER);
    }

    public static MusicalNote ofNameAccidentalOctiveDuration(char name, char accidental, int octave, MusicalDuration duration) {
        return new MusicalNote(MusicalFrequency.of(name, accidental, octave), duration);
    }

    public static MusicalNote fromString(MusicalNote previousNote, String note) {
        if (note == null || note.length() == 0) {
            return null;
        }

        char name = parseName(note, 0, 'c');
        char accidental = parseAccidental(note, 1, '-');
        int octave = parseOctave(note, 2, previousNote.frequency.getOctave());
        MusicalDuration duration = parseDuration(note, 3, MusicalDuration.QUARTER);

        return MusicalNote.ofNameAccidentalOctiveDuration(name, accidental, octave, duration);
    }

    public MusicalFrequency getFrequency() {
        return frequency;
    }

    private static char parseAccidental(String note, int index, char dflt) {
        if (note.length() <= index) {
            return dflt;
        }

        if (note.charAt(index) == '#') {
            return '#';
        }

        if (note.charAt(index) == 'b') {
            return 'b';
        }

        return dflt;
    }

    private static char parseName(String note, int index, char dflt) {
        if (note.length() <= index) {
            return dflt;
        }

        if (note.charAt(index) >= 'a' && note.charAt(index) <= 'g') return note.charAt(index);
        if (note.charAt(index) == 'r') return note.charAt(index);

        return dflt;
    }

    private static int parseOctave(String note, int index, int dflt) {
        int octave;

        if (note.length() <= index) {
            return dflt;
        }

        try {
            octave = Integer.parseInt(note.substring(index, index + 1));
        } catch (NumberFormatException e) {
            octave = dflt;
        }

        return (octave >= 0 && octave <= 8) ? octave : dflt;
    }

    private static MusicalDuration parseDuration(String note, int index, MusicalDuration dflt) {
        if (note.length() <= index) {
            return dflt;
        }

        return MusicalDuration.of(note.substring(index), dflt);
    }

}
