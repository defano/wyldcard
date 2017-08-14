package com.defano.hypercard.gui.sound;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.sound.sampled.*;
import java.util.concurrent.CountDownLatch;

public class MusicPlayer {

    public static void playNotes (Value sound, Value notes, Value tempo) throws HtSemanticException {
        MusicalNote thisNote = MusicalNote.ofMiddleCQuarterNote();
        Sound soundResource = Sound.fromName(sound.stringValue().toLowerCase());

        for (Value thisNoteString : notes.getWords()) {
            thisNote = MusicalNote.fromString(thisNote, thisNoteString.stringValue().toLowerCase());
            playNote(soundResource, thisNote, true);
        }
    }

    private static void playNote(Sound sound, MusicalNote note, boolean synchronously) throws HtSemanticException {

        try {
            AudioInputStream inStream = AudioSystem.getAudioInputStream(sound.getResource());
            AudioFormat inFormat = inStream.getFormat();

            final AudioFormat outFormat = new AudioFormat(
                    inFormat.getEncoding(),
                    Math.round(inFormat.getSampleRate() * note.getFrequency().getFrequenceAdjustment(sound.getDominantFrequency())),
                    inFormat.getSampleSizeInBits(),
                    inFormat.getChannels(),
                    inFormat.getFrameSize(),
                    inFormat.getFrameRate(),
                    inFormat.isBigEndian());

            AudioInputStream outStream = new AudioInputStream(inStream, outFormat, inStream.getFrameLength());

            CountDownLatch cdl = new CountDownLatch(1);

            Clip clip = AudioSystem.getClip();
            clip.open(outStream);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    cdl.countDown();
                }
            });
            clip.start();

            if (synchronously) {
                cdl.await();
            }
        } catch (Exception e) {
            throw new HtSemanticException("The audio system is not available.", e);
        }
    }
}
