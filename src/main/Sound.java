package main;

import com.sun.jdi.FloatType;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class Sound {

    private Clip clip;

    public Sound(String fileName) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/sound/" + fileName);
            AudioInputStream sound = AudioSystem.getAudioInputStream(inputStream);
            // load the sound into memory (a Clip)
            clip = AudioSystem.getClip();
            clip.open(sound);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Malformed URL: " + e);
        }
        catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Unsupported Audio File: " + e);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Input/Output Error: " + e);
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Line Unavailable Exception Error: " + e);
        }
    }

    public void play() {
        clip.setFramePosition(0);
        clip.start();
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        clip.stop();
    }

    public void setVolume(int level) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(level / 5.));
    }

}
