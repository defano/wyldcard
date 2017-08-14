package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.gui.sound.MusicPlayer;
import com.defano.hypertalk.ast.common.MusicSpecifier;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class PlayCmd extends Statement {

    private final Expression sound;
    private final MusicSpecifier ofMusic;

    public PlayCmd(Expression sound, MusicSpecifier ofMusic) {
        this.sound = sound;
        this.ofMusic = ofMusic;
    }

    @Override
    public void execute() throws HtException {
        MusicPlayer.playNotes(sound.evaluate(), ofMusic.notes.evaluate(), null);
    }

}
