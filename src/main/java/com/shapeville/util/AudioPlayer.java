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
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 播放短音效
    public static void playEffect(String resourcePath) {
        try {
            InputStream is = AudioPlayer.class.getResourceAsStream(resourcePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start(); // 不循环
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}