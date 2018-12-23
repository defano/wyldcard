package com.defano.wyldcard.sound;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;

public interface SoundManager {

    /**
     * Attempts to play a sound as a series of musical notes at a given tempo using the playback executor for
     * asynchronous playback.
     *
     * @param sound The name of the sound to play, i.e., "boing", "harpsichord" or "flute"
     * @param notes The sequence of notes to be played in name, accidental, octave and duration format (i.e.,
     *              "c4 d#5h." When empty, the sound sample is played without adjustments to duration or frequency.
     * @param tempo The speed at which to play the notes, specified in quarter notes per minute. When not specified,
     *              a tempo of 120 is assumed.
     */
    void play(Value sound, Value notes, Value tempo);

    void play(SoundSample sample) throws HtSemanticException;

    /**
     * Gets the name of the sound currently playing; when multiple sounds are playing the name of the last enqueued
     * sound is returned. When no sounds are playing, "done" is returned.
     * @return The name of the sound playing or "done" if no sound is playing.
     */
    String getSound();
}
