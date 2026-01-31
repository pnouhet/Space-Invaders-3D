package com.spaceinvaders.engine;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

public class SoundManager {

    private static Clip musicClip;

    public static void playSound(String filename) {
        new Thread(() -> {
            try {
                File f = new File(filename);
                if (!f.exists()) return;

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
                
                clip.addLineListener(e -> {
                    if (e.getType() == LineEvent.Type.STOP) clip.close();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void playMusic(String filename) {
        new Thread(() -> {
            try {
                if (musicClip != null && musicClip.isRunning()) {
                    musicClip.stop();
                    musicClip.close();
                }

                File f = new File(filename);
                if (!f.exists()) {
                    System.err.println("Musique introuvable : " + filename);
                    return;
                }

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
                musicClip = AudioSystem.getClip();
                musicClip.open(audioIn);

                if (musicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(-20.0f); 
                }

                // --- BOUCLE INFINIE ---
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
                
                musicClip.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    // Méthode pour couper le son au Game Over
    public static void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
        }
    }
}