package com.defano.hypertalk.ast.common;

import com.defano.hypertalk.ast.expressions.Expression;

public class MusicSpecifier {

    public final Expression notes;
    public final Expression tempo;

    public static MusicSpecifier forDefault() {
        return new MusicSpecifier(null, null);
    }

    public static MusicSpecifier forNotes(Expression notes) {
        return new MusicSpecifier(notes, null);
    }

    public static MusicSpecifier forTempo(Expression tempo) {
        return new MusicSpecifier(null, tempo);
    }

    public static MusicSpecifier forNotesAndTempo(Expression notes, Expression tempo) {
        return new MusicSpecifier(notes, tempo);
    }

    private MusicSpecifier(Expression notes, Expression tempo) {
        this.notes = notes;
        this.tempo = tempo;
    }
}
