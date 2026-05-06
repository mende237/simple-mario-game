package com.game.mario.sound;

import javax.sound.sampled.*;
import java.io.*;

public class Audio {
    private static Clip backgroundMusicClip;

    public static void playSound(String path) {
        try {
            String resourcePath = path;
            InputStream is = Audio.class.getResourceAsStream(resourcePath);
            if (is != null) {
                InputStream bufferedIn = new BufferedInputStream(is);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } else {
                System.err.println("Audio resource not found: " + resourcePath);
            }
        } catch (Exception e) {
            System.err.println("Error playing sound " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void playSong(String path) {
        playSound(path);
    }

    public static void playBackgroundMusic(String path) {
        try {
            String resourcePath = path;
            InputStream is = Audio.class.getResourceAsStream(resourcePath);
            if (is != null) {
                System.out.println("Background music started: " + resourcePath);
                if (backgroundMusicClip != null && backgroundMusicClip.isRunning())
                    backgroundMusicClip.stop();
                InputStream bufferedIn = new BufferedInputStream(is);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
                backgroundMusicClip = AudioSystem.getClip();
                backgroundMusicClip.open(audioIn);
                backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundMusicClip.start();
            } else {
                System.err.println("Background music resource not found: " + resourcePath);
            }
        } catch (Exception e) {
            System.err.println("Error playing background music " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void stopBackgroundMusic() {
        if (backgroundMusicClip != null)
            backgroundMusicClip.stop();
    }
}
