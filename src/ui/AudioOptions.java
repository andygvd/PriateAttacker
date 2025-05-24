package ui;

import static utilz.Constants.UI.PauseButtons.SOUND_SIZE;
import static utilz.Constants.UI.VolumeButtons.SLIDER_WIDTH;
import static utilz.Constants.UI.VolumeButtons.VOLUME_HEIGHT;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import gamestates.Gamestate;
import main.Game;

public class AudioOptions {

	private VolumeButton volumeButton;
	private SoundButton musicButton, sfxButton;

	private Game game;

	public AudioOptions(Game game) {
		this.game = game;
		initSoundButtons();
		initVolumeButton();
	}

	private void initVolumeButton() {
		int x = (int) (309 * Game.SCALE);
		int y = (int) (278 * Game.SCALE);
		volumeButton = new VolumeButton(x, y, SLIDER_WIDTH, VOLUME_HEIGHT);
	}

	private void initSoundButtons() {
		int soundX = (int) (450 * Game.SCALE);
		musicButton = new SoundButton(soundX, (int) (140 * Game.SCALE), SOUND_SIZE, SOUND_SIZE);
		sfxButton = new SoundButton(soundX, (int) (186 * Game.SCALE), SOUND_SIZE, SOUND_SIZE);
	}

	public void update() {
		musicButton.update();
		sfxButton.update();
		volumeButton.update();
	}

	public void draw(Graphics g) {
		musicButton.draw(g);
		sfxButton.draw(g);
		volumeButton.draw(g);
	}

	public void mouseDragged(MouseEvent e) {
		if (!volumeButton.isMousePressed()) return;

		float oldVal = volumeButton.getFloatValue();
		volumeButton.changeX(e.getX());
		float newVal = volumeButton.getFloatValue();

		if (oldVal != newVal) {
			game.getAudioPlayer().setVolume(newVal);
		}
	}

	public void mousePressed(MouseEvent e) {
		if (isIn(e, musicButton)) {
			musicButton.setMousePressed(true);
			return;
		}
		if (isIn(e, sfxButton)) {
			sfxButton.setMousePressed(true);
			return;
		}
		if (isIn(e, volumeButton)) {
			volumeButton.setMousePressed(true);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (isIn(e, musicButton) && musicButton.isMousePressed()) {
			musicButton.setMuted(!musicButton.isMuted());
			game.getAudioPlayer().toggleSongMute();
		}

		else if (isIn(e, sfxButton) && sfxButton.isMousePressed()) {
			sfxButton.setMuted(!sfxButton.isMuted());
			game.getAudioPlayer().toggleEffectMute();
		}

		musicButton.resetBools();
		sfxButton.resetBools();
		volumeButton.resetBools();
	}

	public void mouseMoved(MouseEvent e) {
		musicButton.setMouseOver(false);
		sfxButton.setMouseOver(false);
		volumeButton.setMouseOver(false);

		if (isIn(e, musicButton)) musicButton.setMouseOver(true);
		else if (isIn(e, sfxButton)) sfxButton.setMouseOver(true);
		else if (isIn(e, volumeButton)) volumeButton.setMouseOver(true);
	}

	private boolean isIn(MouseEvent e, PauseButton b) {
		return b.getBounds().contains(e.getX(), e.getY());
	}
}
