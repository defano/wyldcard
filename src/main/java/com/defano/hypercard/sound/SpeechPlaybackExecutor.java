package com.defano.hypercard.sound;

import com.defano.hypertalk.ast.model.SpeakingVoice;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SpeechPlaybackExecutor extends ThreadPoolExecutor {

    private final static SpeechPlaybackExecutor instance = new SpeechPlaybackExecutor();
    private LocalMaryInterface mary;
    private String theSpeech = "done";

    private SpeechPlaybackExecutor() {
        super(1, 1, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        try {
            this.mary = new LocalMaryInterface();
        } catch (MaryConfigurationException e) {
            this.mary = null;
        }
    }

    public static SpeechPlaybackExecutor getInstance() {
        return instance;
    }

    public Value getTheSpeech() {
        if (getActiveCount() == 0 && getQueue().size() == 0) {
            return new Value("done");
        }

        return new Value(theSpeech);
    }

    public void speak(String text, SpeakingVoice voice) throws HtException {
        if (mary == null) {
            throw new HtSemanticException("Sorry, speaking is not supported on this system.");
        }

        submit(() -> {
            try {
                CountDownLatch latch = new CountDownLatch(1);
                theSpeech = text;

                mary.setVoice(voice.getVoiceId());
                mary.setStreamingAudio(true);

                AudioInputStream audio = mary.generateAudio(text);

                Clip clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        latch.countDown();
                    }
                });
                clip.open(audio);
                clip.start();

                latch.await();

            } catch (SynthesisException | IOException | LineUnavailableException | InterruptedException e) {
                // Nothing useful to do
                e.printStackTrace();
            }
        });
    }
}
