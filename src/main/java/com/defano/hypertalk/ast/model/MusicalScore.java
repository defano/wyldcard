package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.ast.expression.Expression;

public class MusicalScore {

    public final Expression instrument;
    public final Expression notes;
    public final Expression tempo;

    public static MusicalScore ofSoundEffect(Expression instrument) {
        return new MusicalScore(instrument, null, null);
    }

    public static MusicalScore ofNotes(Expression instrument, Expression notes) {
        return new MusicalScore(instrument, notes, null);
    }

    public static MusicalScore ofTempo(Expression instrument, Expression tempo) {
        return new MusicalScore(instrument, null, tempo);
    }

    public static MusicalScore ofNotesAndTempo(Expression instrument, Expression notes, Expression tempo) {
        return new MusicalScore(instrument, notes, tempo);
    }

    private MusicalScore(Expression instrument, Expression notes, Expression tempo) {
        this.instrument = instrument;
        this.notes = notes;
        this.tempo = tempo;
    }
}
