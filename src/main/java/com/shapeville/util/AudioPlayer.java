package com.shapeville.util;

import javax.sound.sampled.*;
import java.io.InputStream;

public class AudioPlayer {
    public static void playWav(String resourcePath) {
        try {
            InputStream is = AudioPlayer.class.getResourceAsStream(resourcePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}