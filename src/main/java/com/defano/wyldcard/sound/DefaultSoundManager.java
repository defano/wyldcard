package com.defano.wyldcard.sound;

import com.defano.wyldcard.WyldCard;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.inject.Singleton;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Singleton
public class DefaultSoundManager implements SoundManager {

    private static final int DEFAULT_TEMPO = 120;
    private static String lastPlayedSound = "";

    @Override
    public void play(Value sound, Value notes, Value tempo) {
        lastPlayedSound = sound.toString();
        SoundPlaybackExecutor.getInstance().submit(() -> playSynchronously(sound, notes, tempo));
    }

    @Override
    public void play(SoundSample sample) throws HtSemanticException {
        try {
            playAudio(getAudioForSample(sample));
        } catch (Exception e) {
            throw new HtSemanticException("An error occurred playing the sound.");
        }
    }

    @Override
    public String getSound() {
        if (SoundPlaybackExecutor.getInstance().getActiveSoundChannelsCount() == 0) {
            return "done";
        } else {
            return DefaultSoundManager.lastPlayedSound;
        }
    }

    /**
     * Gets an AudioInputStream for the given sound sample.
     * @param soundSample The sound to retrieve
     * @return The sound sample data
     * @throws IOException Thrown if an error occurs reading the sound sample.
     * @throws UnsupportedAudioFileException Thrown if the sound sample is in an unplayable format.
     */
    private AudioInputStream getAudioForSample(SoundSample soundSample) throws IOException, UnsupportedAudioFileException {
        return AudioSystem.getAudioInputStream(soundSample.getResource());
    }

    /**
     * Plays the given AudioInputStream synchronously (blocks the current thread until the sound is done).
     *
     * @param audio The audio to play
     * @throws LineUnavailableException Thrown if an error occurs playing the sound
     * @throws IOException Thrown if an error occurs playing the sound
     * @throws InterruptedException Thrown if an error occurs playing the sound
     */
    private void playAudio(AudioInputStream audio) throws LineUnavailableException, IOException, InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);

        Clip clip = AudioSystem.getClip();
        clip.open(audio);
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                cdl.countDown();
            }
        });
        clip.start();

        // Wait for note to finish playing
        cdl.await();
    }

    /**
     * Attempts to play a sound as a series of musical notes at a given tempo synchronously (blocking the current
     * thread while the sound is playing).
     *
     * @param sound The name of the sound to play, i.e., "boing", "harpsichord" or "flute"
     * @param notes The sequence of notes to be played in name, accidental, octave and duration format (i.e.,
     *              "c4 d#5h." When empty, the sound sample is played without adjustments to duration or frequency.
     * @param tempo The speed at which to play the notes, specified in quarter notes per minute. When not specified,
     *              a tempo of 120 is assumed.
     */
    private void playSynchronously(Value sound, Value notes, Value tempo) {
        long playStartTime = System.currentTimeMillis();
        MusicalNote lastNote = MusicalNote.fromMiddleCQuarterNote();

        // Tempo is specified in eighth notes played per minute; convert to beats (whole notes) per minute
        int bpm = tempo.isEmpty() || !tempo.isNumber() ? DEFAULT_TEMPO / 4 : tempo.integerValue() / 4;
        SoundSample soundSampleResource = SoundSample.fromName(sound.toString().toLowerCase());

        // Play each note
        for (String thisNoteString : notes.toString().split("\\s+")) {

            // Break out of playback sequence if user type cmd-. Can't use ExecutionContext here because this
            // executes in a non-handler thread
            if (WyldCard.getInstance().getKeyboardManager().getBreakTime() != null &&
                    WyldCard.getInstance().getKeyboardManager().getBreakTime() > playStartTime)
            {
                return;
            }

            MusicalNote thisNote = MusicalNote.fromString(lastNote, thisNoteString.toLowerCase());

            // Do not inherit octave and duration from rest
            if (thisNote.getFrequency() != MusicalPitch.REST) {
                lastNote = thisNote;
            }

            try {
                playAudio(getAudioForNote(soundSampleResource, thisNote, bpm));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    private AudioInputStream getAudioForNote(SoundSample soundSample, MusicalNote note, int tempoBpm) throws IOException, UnsupportedAudioFileException {
        if (note.getFrequency() == MusicalPitch.REST) {
            AudioInputStream stream = getAudioForSample(SoundSample.SILENCE);
            return transformAudioDuration(SoundSample.SILENCE, stream, note.getDuration(), tempoBpm);
        }
        else {
            AudioInputStream stream = getAudioForSample(soundSample);
            stream = transformAudioFrequency(stream, soundSample.getDominantFrequency(), note.getFrequency());
            return transformAudioDuration(soundSample, stream, note.getDuration(), tempoBpm);
        }
    }

    /**
     * Adjusts the frequency (pitch) at which the given stream will be rendered.
     *
     * @param stream The audio stream whose pitch should be adjusted
     * @param from The current pitch of the sound
     * @param to The desired pitch of the sound
     * @return A new AudioInputStream representing the sound of the input stream adjusted for pitch
     */
    private AudioInputStream transformAudioFrequency(AudioInputStream stream, MusicalPitch from, MusicalPitch to) {
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
    private AudioInputStream transformAudioDuration(SoundSample soundSample, AudioInputStream stream, MusicalDuration duration, int tempoBpm) throws IOException {
        double durationMs = duration.getDurationMs(tempoBpm);
        double samplesPerMs = stream.getFormat().getSampleRate() / 1000.0;
        double framesForDuration = samplesPerMs * durationMs;

        // Sample is longer than desired duration; truncate
        if (framesForDuration < stream.getFrameLength()) {
            return new AudioInputStream(stream, stream.getFormat(), Math.round(framesForDuration));
        }

        // Sample is shorter than desired duration; stretch or silence-append
        else if (framesForDuration > stream.getFrameLength()) {
            if (soundSample.isStretchable()) {
                return stretchAudio(soundSample, stream, (int) framesForDuration);
            } else {
                return appendSilenceToAudio(stream, (int) framesForDuration);
            }
        }

        // What are the odds?! Sample is perfectly sized
        else {
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
    private AudioInputStream appendSilenceToAudio(AudioInputStream stream, int desiredFrames) throws IOException {
        // Create buffer for stretched sound
        byte[] stretched = new byte[stream.getFormat().getFrameSize() * desiredFrames];

        // Read sound sample into memory
        byte[] sample = new byte[(int) (stream.getFrameLength() * stream.getFormat().getFrameSize())];
        stream.read(sample, 0, (int) (stream.getFrameLength() * stream.getFormat().getFrameSize()));

        // Write sample to buffer
        System.arraycopy(sample, 0, stretched, 0, sample.length);

        // Pad remaining frames with silencio
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
    private AudioInputStream stretchAudio(SoundSample soundSample, AudioInputStream stream, int desiredFrames) throws IOException {
        int frameSize = stream.getFormat().getFrameSize();              // Bytes per frame
        int stretchedIdx = 0;

        // Create buffer for stretched soundSample
        byte[] stretched = new byte[frameSize * desiredFrames];

        // Read soundSample sample into memory
        byte[] sample = new byte[(int) (stream.getFrameLength() * frameSize)];
        int sampleLength = stream.read(sample, 0, (int) (stream.getFrameLength() * frameSize));

        int introIndex = soundSample.getLoopStart() * frameSize;        // Index in sample where loop starts
        int outroIndex = soundSample.getLoopEnd() * frameSize;          // Index in sample where loop ends, inclusive
        int outroLength = sampleLength - outroIndex;                    // Length of release
        int loopLength = (soundSample.getLoopEnd() - soundSample.getLoopStart()) * frameSize;

        // Append intro section (attack) to output buffer
        for (int sampleIdx = 0; sampleIdx < introIndex; sampleIdx++) {
            stretched[stretchedIdx++] = sample[sampleIdx];
        }

        // Loop sample to output
        while (stretchedIdx < stretched.length - (outroLength + loopLength)) {
            for (int sampleIdx = introIndex; sampleIdx < outroIndex + frameSize && stretchedIdx < stretched.length - outroLength; sampleIdx++) {
                stretched[stretchedIdx++] = sample[sampleIdx];
            }
        }

        // Append outro section (release) to output
        for (int sampleIdx = outroIndex + frameSize; sampleIdx < sampleLength; sampleIdx++) {
            stretched[stretchedIdx++] = sample[sampleIdx];
        }

        // Pad remainder with silence
        while (stretchedIdx < stretched.length) {
            stretched[stretchedIdx++] = 0;
        }

        return new AudioInputStream(new ByteArrayInputStream(stretched), stream.getFormat(), desiredFrames);
    }

}
