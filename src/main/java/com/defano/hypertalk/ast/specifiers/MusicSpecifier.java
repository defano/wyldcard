package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.expressions.Expression;

public class MusicSpecifier {

    public final Expression instrument;
    public final Expression notes;
    public final Expression tempo;

    public static MusicSpecifier forDefault(Expression instrument) {
        return new MusicSpecifier(instrument, null, null);
    }

    public static MusicSpecifier forNotes(Expression instrument, Expression notes) {
        return new MusicSpecifier(instrument, notes, null);
    }

    public static MusicSpecifier forTempo(Expression instrument, Expression tempo) {
        return new MusicSpecifier(instrument, null, tempo);
    }

    public static MusicSpecifier forNotesAndTempo(Expression instrument, Expression notes, Expression tempo) {
        return new MusicSpecifier(instrument, notes, tempo);
    }

    private MusicSpecifier(Expression instrument, Expression notes, Expression tempo) {
        this.instrument = instrument;
        this.notes = notes;
        this.tempo = tempo;
    }
}
