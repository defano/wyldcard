package com.defano.hypercard.gui.sound;

public class MusicalNote {

    private final MusicalPitch frequency;
    private final MusicalDuration duration;

    public MusicalNote (MusicalPitch frequency, MusicalDuration duration) {
        this.frequency = frequency;
        this.duration = duration;
    }

    public static MusicalNote fromMiddleCQuarterNote() {
        return new MusicalNote(MusicalPitch.C4, MusicalDuration.QUARTER);
    }

    public static MusicalNote fromNameAccidentalOctaveDuration(char name, char accidental, int octave, MusicalDuration duration) {
        return new MusicalNote(MusicalPitch.of(name, accidental, octave), duration);
    }

    public static MusicalNote fromString(MusicalNote previousNote, String note) {
        if (note == null || note.length() == 0) {
            return previousNote;
        }

        int index = 0;

        Character name = parseName(note, index);
        Character accidental = parseAccidental(note, name != null ? ++index : index);
        Integer octave = parseOctave(note, accidental != null ? ++ index : index);
        MusicalDuration duration = parseDuration(note, octave != null ? ++index : index);

        return MusicalNote.fromNameAccidentalOctaveDuration(
                name == null ? previousNote.getFrequency().getName() : name,
                accidental == null ? '-' : accidental,
                octave == null ? previousNote.getFrequency().getOctave() : octave,
                duration == null ? previousNote.duration : duration);
    }

    public MusicalPitch getFrequency() {
        return frequency;
    }

    public MusicalDuration getDuration() {
        return duration;
    }

    private static Character parseAccidental(String note, int index) {
        if (note.length() <= index) {
            return null;
        }

        if (note.substring(index).contains("#")) {
            return '#';
        }

        if (note.substring(index).contains("b")) {
            return 'b';
        }

        return null;
    }

    private static Character parseName(String note, int index) {
        if (note.length() <= index) {
            return null;
        }

        if (note.charAt(index) >= 'a' && note.charAt(index) <= 'g') return note.charAt(index);
        if (note.charAt(index) == 'r') return note.charAt(index);

        return null;
    }

    private static Integer parseOctave(String note, int index) {
        int octave;

        if (note.length() <= index) {
            return null;
        }

        try {
            octave = Integer.parseInt(note.substring(index, index + 1));
        } catch (NumberFormatException e) {
            octave = -1;
        }

        return (octave >= 0 && octave <= 8) ? octave : null;
    }

    private static MusicalDuration parseDuration(String note, int index) {
        if (note.length() <= index) {
            return null;
        }

        return MusicalDuration.fromString(note.substring(index));
    }

    @Override
    public String toString() {
        return "MusicalNote{" +
                "frequency=" + frequency +
                ", duration=" + duration +
                '}';
    }
}
