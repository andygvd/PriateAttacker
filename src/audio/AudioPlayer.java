package audio;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.*;

public class AudioPlayer {

	public static int MENU_1 = 0;
	public static int LEVEL_1 = 1;
	public static int LEVEL_2 = 2;

	public static int DIE = 0;
	public static int JUMP = 1;
	public static int GAMEOVER = 2;
	public static int LVL_COMPLETED = 3;
	public static int ATTACK_ONE = 4;
	public static int ATTACK_TWO = 5;
	public static int ATTACK_THREE = 6;

	private Clip[] songs, effects;
	private int currentSongId;
	private float volume = 0.5f;
	private boolean songMute, effectMute;
	private Random rand = new Random();

	public AudioPlayer() {
		loadSongs();
		loadEffects();
		playSong(MENU_1);
	}

	private void loadSongs() {
		String[] names = { "menu", "level1", "level2" };
		songs = new Clip[names.length];

		for (int i = 0; i < names.length; i++) {
			songs[i] = getClip(names[i]);
		}
	}

	private void loadEffects() {
		String[] effectNames = {
			"die", "jump", "gameover", "lvlcompleted", 
			"attack1", "attack2", "attack3"
		};
		effects = new Clip[effectNames.length];

		for (int i = 0; i < effectNames.length; i++) {
			effects[i] = getClip(effectNames[i]);
		}

		updateEffectsVolume();
	}

	private Clip getClip(String name) {
		try {
			URL resource = getClass().getResource("/audio/" + name + ".wav");
			if (resource == null) throw new IOException("Audio file not found: " + name);
			
			AudioInputStream stream = AudioSystem.getAudioInputStream(resource);
			Clip clip = AudioSystem.getClip();
			clip.open(stream);
			return clip;
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setVolume(float volume) {
		this.volume = volume;
		updateSongVolume();
		updateEffectsVolume();
	}

	public void stopSong() {
		if (songs[currentSongId] != null && songs[currentSongId].isRunning()) {
			songs[currentSongId].stop();
		}
	}

	public void setLevelSong(int lvlIndex) {
		playSong((lvlIndex % 2 == 0) ? LEVEL_1 : LEVEL_2);
	}

	public void lvlCompleted() {
		stopSong();
		playEffect(LVL_COMPLETED);
	}

	public void playAttackSound() {
		int attackIndex = ATTACK_ONE + rand.nextInt(3);
		playEffect(attackIndex);
	}

	public void playEffect(int effect) {
		if (effect >= 0 && effect < effects.length && effects[effect] != null) {
			effects[effect].setMicrosecondPosition(0);
			effects[effect].start();
		}
	}

	public void playSong(int song) {
		stopSong();
		currentSongId = song;
		updateSongVolume();

		if (songs[currentSongId] != null) {
			songs[currentSongId].setMicrosecondPosition(0);
			songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY);
		}
	}

	public void toggleSongMute() {
		songMute = !songMute;

		for (Clip clip : songs) {
			if (clip != null && clip.isControlSupported(BooleanControl.Type.MUTE)) {
				BooleanControl mute = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
				mute.setValue(songMute);
			}
		}
	}

	public void toggleEffectMute() {
		effectMute = !effectMute;

		for (Clip clip : effects) {
			if (clip != null && clip.isControlSupported(BooleanControl.Type.MUTE)) {
				BooleanControl mute = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
				mute.setValue(effectMute);
			}
		}

		if (!effectMute) {
			playEffect(JUMP);
		}
	}

	private void updateSongVolume() {
		if (songs[currentSongId] == null || !songs[currentSongId].isControlSupported(FloatControl.Type.MASTER_GAIN))
			return;

		FloatControl volumeControl = (FloatControl) songs[currentSongId].getControl(FloatControl.Type.MASTER_GAIN);
		volumeControl.setValue(calculateGain(volumeControl));
	}

	private void updateEffectsVolume() {
		for (Clip clip : effects) {
			if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				vol.setValue(calculateGain(vol));
			}
		}
	}

	private float calculateGain(FloatControl ctrl) {
		float min = ctrl.getMinimum();
		float max = ctrl.getMaximum();
		return (max - min) * volume + min;
	}
}
