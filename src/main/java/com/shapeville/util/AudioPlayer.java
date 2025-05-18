package com.shapeville.util;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AudioPlayer {
    private static Clip backgroundMusic;
    private static List<Clip> activeClips = new ArrayList<>();

    public static void playWav(String resourcePath) {
        try {
            if (backgroundMusic != null) {
                backgroundMusic.stop();
                backgroundMusic.close();
            }
            
            InputStream is = AudioPlayer.class.getResourceAsStream(resourcePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
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
            
            // 添加监听器，在播放完成后自动清理资源
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    activeClips.remove(clip);
                }
            });
            
            activeClips.add(clip);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 停止所有音频
    public static void stopAll() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.close();
            backgroundMusic = null;
        }
        
        for (Clip clip : activeClips) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.close();
            }
        }
        activeClips.clear();
    }
}