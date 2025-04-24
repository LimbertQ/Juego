/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fix;

/**
 *
 * @author pc
 */
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {

    private Clip clip;
    private FloatControl volume;

    public Sound(Clip clip) {
        this.clip = clip;
        volume = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
    }

    public void play() {
        clip.setFramePosition(0);
        clip.start();
    }

    public void loop() {
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        clip.stop();
    }

    public int getFramePosition() {
        return clip.getFramePosition();
    }

    public void changeVolume(float value) {
        volume.setValue(value);
    }

}
