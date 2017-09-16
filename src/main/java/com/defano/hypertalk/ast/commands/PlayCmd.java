package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.sound.SoundPlayer;
import com.defano.hypertalk.ast.common.MusicSpecifier;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;

public class PlayCmd extends Command {

    private final Expression sound;
    private final MusicSpecifier ofMusic;

    public PlayCmd(Expression sound, MusicSpecifier ofMusic) {
        super("play");

        this.sound = sound;
        this.ofMusic = ofMusic;
    }

    @Override
    public void onExecute() throws HtException {
        Value notes = ofMusic.notes == null ? new Value() : ofMusic.notes.evaluate();
        Value tempo = ofMusic.tempo == null ? new Value() : ofMusic.tempo.evaluate();

        SoundPlayer.play(sound.evaluate(), notes, tempo);
    }
}
