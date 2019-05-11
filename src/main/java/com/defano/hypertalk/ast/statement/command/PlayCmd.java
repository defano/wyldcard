package com.defano.hypertalk.ast.statement.command;

import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.hypertalk.ast.model.MusicalScore;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.sound.SoundManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class PlayCmd extends Command {

    @Inject
    private SoundManager soundManager;

    private final MusicalScore music;

    public PlayCmd(ParserRuleContext context, MusicalScore music) {
        super(context, "play");
        this.music = music;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        Value instrument = music.instrument.evaluate(context);
        Value notes = music.notes == null ? new Value() : music.notes.evaluate(context);
        Value tempo = music.tempo == null ? new Value() : music.tempo.evaluate(context);

        soundManager.play(instrument, notes, tempo);
    }
}
