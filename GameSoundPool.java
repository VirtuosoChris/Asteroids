
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GameSoundPool {

    private List<Clip> clipPool = new ArrayList<>();
    private String soundFileName;

    public GameSoundPool(String soundFileName) {
        this.soundFileName = soundFileName;
        reserve(2);
    }

    private Clip createClip() {
        try {
            URL soundURL = new File(soundFileName).toURI().toURL();

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Clip getAvailableClip() {
        for (Clip clip : clipPool) {
            if (!clip.isRunning()) {
                return clip;
            }
        }

        Clip newClip = createClip();
        if (newClip != null) {
            clipPool.add(newClip);
        }
        return newClip;
    }

    public Clip play() {
        Clip clip = getAvailableClip();

        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }

        return clip;
    }

    public Clip start() {
        return play();
    }

    public void reserve(int count) {
        int reserveAmount = count - clipPool.size();

        while (reserveAmount > 0) {

            Clip newClip = createClip();
            if (newClip != null) {
                clipPool.add(newClip);
            }

            reserveAmount--;
        }

    }

    public void stopAll() {
        for (Clip clip : clipPool) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }

    public void loop() {
        loop(-1);
    }

    public void loop(int count) {
        Clip clip = getAvailableClip();
        if (clip != null) {
            clip.loop(count); // Loop the sound 'count' times
        }
    }

}
