package com.defano.hypercard.gui.sound;

import com.defano.hypercard.gui.util.KeyboardManager;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class MusicPlayer {

    private static final int DEFAULT_TEMPO = 120;

    /**
     * Attempts to play a sound as a series of musical notes at a given tempo.
     *
     * @param sound The name of the sound to play, i.e., "boing", "harpsichord" or "flute"
     * @param notes The sequence of notes to be played in name, accidental, octave and duration format (i.e.,
     *              "c4 d#5h." When empty, the sound sample is played without adjustments to duration or frequency.
     * @param tempo The speed at which to play the notes, specified in quarter notes per minute. When not specified,
     *              a tempo of 120 is assumed.
     * @throws HtSemanticException Thrown if an error occurs while playing the sound.
     */
    public static void playNotes(Value sound, Value notes, Value tempo) throws HtSemanticException {
        MusicalNote lastNote = MusicalNote.fromMiddleCQuarterNote();

        // Tempo is specified in eighth notes played per minute; convert to beats (whole notes) per minute
        int bpm = tempo.isEmpty() || !tempo.isNumber() ? DEFAULT_TEMPO / 4 : tempo.integerValue() / 4;
        SoundSample soundSampleResource = SoundSample.fromName(sound.stringValue().toLowerCase());

        // Play each note
        for (String thisNoteString : notes.stringValue().split("\\s+")) {

            // Break out of playback sequence if user type cmd-.
            if (KeyboardManager.isBreakSequence) return;

            MusicalNote thisNote = MusicalNote.fromString(lastNote, thisNoteString.toLowerCase());

            // Do not inherit octave and duration from rest
            if (thisNote.getFrequency() != MusicalPitch.REST) {
                lastNote = thisNote;
            }

            playNote(soundSampleResource, thisNote, bpm);
        }
    }

    /**
     * Plays the given sound sample as a musical note at the specified tempo. Programmatically adjusts the sound sample
     * to the frequency and duration specified by the note and tempo.
     *
     * @param soundSample The sound sample to play
     * @param note The musical note at which to play
     * @param tempoBpm The speed at which the note is played in beats per minute
     * @throws HtSemanticException
     */
    private static void playNote(SoundSample soundSample, MusicalNote note, int tempoBpm) throws HtSemanticException {

        try {
            CountDownLatch cdl = new CountDownLatch(1);

            Clip clip = AudioSystem.getClip();
            clip.open(getAudioForNote(soundSample, note, tempoBpm));
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    cdl.countDown();
                }
            });
            clip.start();

            // Wait for note to finish playing
            cdl.await();

        } catch (Exception e) {
            throw new HtSemanticException("An error occurred while trying to play the sound.", e);
        }
    }

    /**
     * Gets an AudioInputStream (a stream of sound bytes) representing the given sample being played at the requested
     * note and tempo.
     *
     * @param soundSample The sound sample to be play
     * @param note The musical note at which to play it
     * @param tempoBpm The speed at which the note is played in beats per minute
     * @return An AudioInputStream representing the frequency and duration-adjusted sample.
     * @throws IOException Thrown if an error occurs reading the sound sample.
     * @throws UnsupportedAudioFileException Thrown if the sound sample is in an unplayable format.
     */
    private static AudioInputStream getAudioForNote(SoundSample soundSample, MusicalNote note, int tempoBpm) throws IOException, UnsupportedAudioFileException {
        if (note.getFrequency() == MusicalPitch.REST) {
            AudioInputStream stream = getAudioForSample(SoundSample.SILENCE);
            return transformStreamDuration(SoundSample.SILENCE, stream, note.getDuration(), tempoBpm);
        }
        else {
            AudioInputStream stream = getAudioForSample(soundSample);
            stream = transformStreamFrequency(stream, soundSample.getDominantFrequency(), note.getFrequency());
            return transformStreamDuration(soundSample, stream, note.getDuration(), tempoBpm);
        }
    }

    /**
     * Gets an AudioInputStream for the given sound sample.
     * @param soundSample The sound to retrieve
     * @return The sound sample data
     * @throws IOException Thrown if an error occurs reading the sound sample.
     * @throws UnsupportedAudioFileException Thrown if the sound sample is in an unplayable format.
     */
    private static AudioInputStream getAudioForSample(SoundSample soundSample) throws IOException, UnsupportedAudioFileException {
        return AudioSystem.getAudioInputStream(soundSample.getResource());
    }

    /**
     * Adjusts the frequency (pitch) at which the given stream will be rendered.
     *
     * @param stream The audio stream whose pitch should be adjusted
     * @param from The current pitch of the sound
     * @param to The desired pitch of the sound
     * @return A new AudioInputStream representing the sound of the input stream adjusted for pitch
     */
    private static AudioInputStream transformStreamFrequency(AudioInputStream stream, MusicalPitch from, MusicalPitch to) {
        if (from == to) {
            return stream;
        }

        AudioFormat inFormat = stream.getFormat();
        final AudioFormat outFormat = new AudioFormat(
                inFormat.getEncoding(),
                Math.round(inFormat.getSampleRate() * to.getFrequencyAdjustment(from)),
                inFormat.getSampleSizeInBits(),
                inFormat.getChannels(),
                inFormat.getFrameSize(),
                inFormat.getFrameRate(),
                inFormat.isBigEndian());

        return new AudioInputStream(stream, outFormat, stream.getFrameLength());
    }

    /**
     * Adjusts the duration (length) of the sound sample as "best as possible."
     *
     * When reducing the length of a sample, the sample is clipped to the requested duration.
     *
     * When enlarging the length of a sample, if the sample is "stretchable" (meaning the sample has a defined loop
     * points), the sample is stretched by repeating the loopable section of the sample in the output. If the sample
     * is not stretchable, then it is enlarged by appending silence to the end of it.
     *
     * @param soundSample The sample whose duration should be adjusted.
     * @param stream An AudioInputStream representing a playback stream of this sound.
     * @param duration The requested duration of the sample
     * @param tempoBpm The playback tempo, in beats per minute
     * @return A new AudioInputStream transformed for duration
     * @throws IOException Thrown if an error occurs reading the sound sample.
     */
    private static AudioInputStream transformStreamDuration(SoundSample soundSample, AudioInputStream stream, MusicalDuration duration, int tempoBpm) throws IOException {
        double durationMs = duration.getDurationMs(tempoBpm);
        double framesPerMs = stream.getFormat().getFrameRate() / 1000.0;
        double framesForDuration = framesPerMs * durationMs;

        if (framesForDuration < stream.getFrameLength()) {
            return new AudioInputStream(stream, stream.getFormat(), Math.round(framesForDuration));
        } else if (framesForDuration > stream.getFrameLength()) {
            if (soundSample.isStretchable()) {
                return stretchAudio(soundSample, stream, (int) framesForDuration);
            } else {
                return appendSilenceToAudio(stream, (int) framesForDuration);
            }
        } else {
            return stream;
        }
    }

    /**
     * Appends a specified number of silent frames to the given audio stream.
     * @param stream The stream to append.
     * @param desiredFrames The number of desired silence frames to append.
     * @return A new AudioInputStream containing the appended silence
     * @throws IOException Thrown if an error occurs reading the sound sample.
     */
    private static AudioInputStream appendSilenceToAudio(AudioInputStream stream, int desiredFrames) throws IOException {
        // Create buffer for stretched sound
        byte[] stretched = new byte[stream.getFormat().getFrameSize() * desiredFrames];

        // Read sound sample into memory
        byte[] sample = new byte[(int) (stream.getFrameLength() * stream.getFormat().getFrameSize())];
        stream.read(sample, 0, (int) (stream.getFrameLength() * stream.getFormat().getFrameSize()));

        for (int index = 0; index < sample.length; index++) {
            stretched[index] = sample[index];
        }

        for (int index = sample.length; index < desiredFrames; index++) {
            stretched[index] = 0;
        }

        return new AudioInputStream(new ByteArrayInputStream(stretched), stream.getFormat(), desiredFrames);
    }

    /**
     * Stretches an audio sample by continuously repeating the sample's loop until the requested duration has been
     * reached.
     *
     * @param soundSample The sound sample to stretch. Sample must be "strechable" (have defined loop points)
     * @param stream An AudioInputStream representing the sample.
     * @param desiredFrames The desired length of the stream, in frames.
     * @return A new AudioInputStream stretched to match the requested length.
     * @throws IOException Thrown if an error occurs reading the sound sample.
     */
    private static AudioInputStream stretchAudio(SoundSample soundSample, AudioInputStream stream, int desiredFrames) throws IOException {

        // Create buffer for stretched soundSample
        byte[] stretched = new byte[stream.getFormat().getFrameSize() * desiredFrames];

        // Read soundSample sample into memory
        byte[] sample = new byte[(int) (stream.getFrameLength() * stream.getFormat().getFrameSize())];
        stream.read(sample, 0, (int) (stream.getFrameLength() * stream.getFormat().getFrameSize()));

        int introLength = soundSample.getLoopStart();
        int outroLength = sample.length - soundSample.getLoopEnd();

        // Append intro section (attack) to output buffer
        int stretchedIdx = 0;
        for (int sampleIdx = 0; sampleIdx < introLength; sampleIdx++) {
            stretched[stretchedIdx++] = sample[sampleIdx];
        }

        // Loop sample to output
        do {
            for (int sampleIdx = introLength; sampleIdx <= soundSample.getLoopEnd() && stretchedIdx < stretched.length - outroLength; sampleIdx++) {
                stretched[stretchedIdx++] = sample[sampleIdx];
            }
        } while (stretchedIdx < stretched.length - outroLength);

        // Append outro section (release) to output
        for (int sampleIdx = soundSample.getLoopEnd() + 1; sampleIdx < sample.length && stretchedIdx < stretched.length; sampleIdx++) {
            stretched[stretchedIdx++] = sample[sampleIdx];
        }

        return new AudioInputStream(new ByteArrayInputStream(stretched), stream.getFormat(), desiredFrames);
    }

}
