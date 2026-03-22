package com.unisa.seproject.model.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Plays a WAV audio file asynchronously on the host machine.
 * Playback is non-blocking: the clip starts and the method returns immediately.
 * A {@link javax.sound.sampled.LineListener} closes all resources when the clip stops.
 *
 * <p>JSON example: {@code {"type":"AUDIO","filePath":"/sounds/alert.wav"}}
 */
public record AudioAction(String filePath) implements Action {

    private static final Logger log = LoggerFactory.getLogger(AudioAction.class);

    @Override
    public void execute() {
        File audioFile = new File(filePath);
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        stream.close();
                    } catch (IOException ignored) {
                    }
                }
            });
            clip.start();
            log.info("[AudioAction] Playing '{}'", filePath);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            log.error("[AudioAction] Failed to play '{}': {}", filePath, e.getMessage());
        }
    }
}
