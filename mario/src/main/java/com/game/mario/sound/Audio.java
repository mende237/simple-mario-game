package com.game.mario.sound;

import javax.sound.sampled.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Audio {
    private static Clip backgroundMusicClip;
    private static final ExecutorService soundExecutor = Executors.newCachedThreadPool();
    private static final ExecutorService bgmExecutor = Executors.newSingleThreadExecutor();

    public static void playSound(String path) {
        soundExecutor.execute(() -> {
            try {
                InputStream is = Audio.class.getResourceAsStream(path);
                if (is != null) {
                    InputStream bufferedIn = new BufferedInputStream(is);
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
                    Clip clip = AudioSystem.getClip();

                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                            try {
                                audioIn.close();
                            } catch (IOException e) {
                                // Ignore cleanup errors
                            }
                        }
                    });

                    clip.open(audioIn);
                    clip.start();
                } else {
                    System.err.println("Audio resource not found: " + path);
                }
            } catch (Exception e) {
                System.err.println("Error playing sound " + path + ": " + e.getMessage());
            }
        });
    }

    public static void playSong(String path) {
        playSound(path);
    }

    public static void playBackgroundMusic(String path) {
        bgmExecutor.execute(() -> {
            try {
                if (backgroundMusicClip != null) {
                    backgroundMusicClip.stop();
                    backgroundMusicClip.close();
                }

                InputStream is = Audio.class.getResourceAsStream(path);
                if (is != null) {
                    InputStream bufferedIn = new BufferedInputStream(is);
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
                    backgroundMusicClip = AudioSystem.getClip();
                    backgroundMusicClip.open(audioIn);
                    backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                    backgroundMusicClip.start();
                    System.out.println("Background music started: " + path);
                } else {
                    System.err.println("Background music resource not found: " + path);
                }
            } catch (Exception e) {
                System.err.println("Error playing background music " + path + ": " + e.getMessage());
            }
        });
    }

    public static void stopBackgroundMusic() {
        bgmExecutor.execute(() -> {
            if (backgroundMusicClip != null) {
                backgroundMusicClip.stop();
                backgroundMusicClip.close();
                backgroundMusicClip = null;
            }
        });
    }
}
