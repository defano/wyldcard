package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.sound.SoundPlayer;
import com.defano.hypertalk.ast.specifiers.MusicSpecifier;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PlayCmd extends Command {

    private final MusicSpecifier music;

    public PlayCmd(ParserRuleContext context, MusicSpecifier music) {
        super(context, "play");
        this.music = music;
    }

    @Override
    public void onExecute() throws HtException {
        Value instrument = music.instrument.evaluate();
        Value notes = music.notes == null ? new Value() : music.notes.evaluate();
        Value tempo = music.tempo == null ? new Value() : music.tempo.evaluate();

        SoundPlayer.play(instrument, notes, tempo);
    }
}
