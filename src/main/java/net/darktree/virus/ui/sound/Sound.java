package net.darktree.virus.ui.sound;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {

	public final Clip clip;
	public final float baseVolume;

	public Sound(Clip clip, float volume) {
		this.clip = clip;
		this.baseVolume = volume;
	}

	public void play(float volume) {

		if (Sounds.isEnabled() && clip != null) {
			//new Thread(() -> {
			FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
			float range = volumeControl.getMaximum() - volumeControl.getMinimum();
			float gain = (range * baseVolume * volume) + volumeControl.getMinimum();
			volumeControl.setValue(gain);

			clip.stop();
			clip.setFramePosition(0);
			clip.start();
			//}).start();
		}
	}

	public void play() {
		play(1.0f);
	}

}
