package com.defano.hypercard.gui.sound;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import sun.audio.AudioPlayer;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

public class MusicPlayer {

    public static void playNotes (Value sound, Value notes, Value tempo) throws HtSemanticException {
        MusicalNote lastNote = MusicalNote.fromMiddleCQuarterNote();
        int bpm = tempo.isEmpty() || !tempo.isNumber() ? 120 / 4 : tempo.integerValue() / 4;
        Sound soundResource = Sound.fromName(sound.stringValue().toLowerCase());

        for (String thisNoteString : notes.stringValue().split("\\s+")) {
            MusicalNote thisNote = MusicalNote.fromString(lastNote, thisNoteString.toLowerCase());
            if (thisNote.getFrequency() == MusicalFrequency.R) {
                try {
                    Thread.sleep((long) thisNote.getDuration().getDurationMs(bpm));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            System.err.println(thisNote);
            lastNote = thisNote;
            playNote(soundResource, thisNote, bpm,true);
        }
    }

    private static void playNote(Sound sound, MusicalNote note, int bpm, boolean synchronously) throws HtSemanticException {

        try {

            AudioInputStream stream = getStreamForSound(sound);
            stream = adjustStreamForFrequency(stream, sound.getDominantFrequency(), note.getFrequency());
            stream = adjustStreamForDuration(sound, stream, note.getDuration(), bpm);

            CountDownLatch cdl = new CountDownLatch(1);

            Clip clip = AudioSystem.getClip();
            clip.open(stream);
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

    private static AudioInputStream getStreamForSound(Sound sound) throws IOException, UnsupportedAudioFileException {
        return AudioSystem.getAudioInputStream(sound.getResource());
    }

    private static AudioInputStream adjustStreamForFrequency(AudioInputStream stream, MusicalFrequency from, MusicalFrequency to) {
        AudioFormat inFormat = stream.getFormat();
        final AudioFormat outFormat = new AudioFormat(
                inFormat.getEncoding(),
                Math.round(inFormat.getSampleRate() * to.getFrequenceAdjustment(from)),
                inFormat.getSampleSizeInBits(),
                inFormat.getChannels(),
                inFormat.getFrameSize(),
                inFormat.getFrameRate(),
                inFormat.isBigEndian());

        return new AudioInputStream(stream, outFormat, stream.getFrameLength());
    }

    private static AudioInputStream adjustStreamForDuration(Sound sound, AudioInputStream stream, MusicalDuration duration, int bpm) throws IOException {
        double durationMs = duration.getDurationMs(bpm);
        double framesPerMs = stream.getFormat().getFrameRate() / 1000.0;
        int framesForDuration = (int)(framesPerMs * durationMs);

        if (framesForDuration < stream.getFrameLength()) {
            return new AudioInputStream(stream, stream.getFormat(), Math.round(framesForDuration));
        } else if (sound.isStretchable() && framesForDuration > stream.getFrameLength()) {
            int stretchedIdx = 0;

            // Create buffer for stretched sound
            byte[] stretched = new byte[stream.getFormat().getFrameSize() * framesForDuration];

            // Read sound sample into memory
            byte[] sample = new byte[(int)(stream.getFrameLength() * stream.getFormat().getFrameSize())];
            stream.read(sample, 0, (int) (stream.getFrameLength() * stream.getFormat().getFrameSize()));

            // Append intro section (attack) to output buffer
            for (int sampleIdx = 0; sampleIdx < sound.getLoopStart(); sampleIdx++) {
                stretched[stretchedIdx++] = sample[sampleIdx];
            }

            // Loop sample to output
            do {
                for (int sampleIdx = sound.getLoopStart(); sampleIdx <= sound.getLoopEnd() && stretchedIdx < stretched.length - sound.getLoopEnd(); sampleIdx++) {
                    if (sampleIdx == sound.getLoopStart()) System.err.println("Start: " + sample[sampleIdx]);
                    if (sampleIdx == sound.getLoopEnd()) System.err.println("End: " + sample[sampleIdx]);
                    stretched[stretchedIdx++] = sample[sampleIdx];
                }
            } while (stretchedIdx < stretched.length - sound.getLoopEnd());

            // Append outro section (release) to output
            for (int sampleIdx = sound.getLoopEnd() + 1; sampleIdx < sample.length && stretchedIdx < stretched.length; sampleIdx++) {
                stretched[stretchedIdx++] = sample[sampleIdx];
            }

            return new AudioInputStream(new ByteArrayInputStream(stretched), stream.getFormat(), framesForDuration);
        }
        else {
            return stream;
        }
    }
}
