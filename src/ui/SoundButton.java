package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import utilz.LoadSave;
import static utilz.Constants.UI.PauseButtons.*;

public class SoundButton extends PauseButton {

	private BufferedImage[][] soundImgs;
	private boolean mouseOver = false;
	private boolean mousePressed = false;
	private boolean muted = false;

	private int rowIndex = 0;
	private int colIndex = 0;

	public SoundButton(int x, int y, int width, int height) {
		super(x, y, width, height);
		loadSoundImages();
	}

	private void loadSoundImages() {
		BufferedImage atlas = LoadSave.GetSpriteAtlas(LoadSave.SOUND_BUTTONS);
		soundImgs = new BufferedImage[2][3];

		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 3; col++) {
				int x = col * SOUND_SIZE_DEFAULT;
				int y = row * SOUND_SIZE_DEFAULT;
				soundImgs[row][col] = atlas.getSubimage(x, y, SOUND_SIZE_DEFAULT, SOUND_SIZE_DEFAULT);
			}
		}
	}

	public void update() {
		rowIndex = muted ? 1 : 0;
		colIndex = mousePressed ? 2 : (mouseOver ? 1 : 0);
	}

	public void draw(Graphics g) {
		g.drawImage(soundImgs[rowIndex][colIndex], x, y, width, height, null);
	}

	public void resetBools() {
		mouseOver = false;
		mousePressed = false;
	}

	// === Getters and Setters ===

	public boolean isMouseOver() {
		return mouseOver;
	}

	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}

	public boolean isMousePressed() {
		return mousePressed;
	}

	public void setMousePressed(boolean mousePressed) {
		this.mousePressed = mousePressed;
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}
}
