package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import utilz.LoadSave;

import static utilz.Constants.UI.VolumeButtons.*;

public class VolumeButton extends PauseButton {

	private BufferedImage[] imgs;
	private BufferedImage slider;

	private int index = 0;
	private int buttonX;
	private int minX, maxX;

	private boolean mouseOver = false;
	private boolean mousePressed = false;

	private float floatValue = 0f;

	public VolumeButton(int x, int y, int width, int height) {
		super(x + width / 2, y, VOLUME_WIDTH, height);

		this.x = x;
		this.width = width;
		buttonX = x + width / 2;

		minX = x + VOLUME_WIDTH / 2;
		maxX = x + width - VOLUME_WIDTH / 2;

		bounds.x -= VOLUME_WIDTH / 2;

		loadImages();
	}

	private void loadImages() {
		BufferedImage atlas = LoadSave.GetSpriteAtlas(LoadSave.VOLUME_BUTTONS);
		imgs = new BufferedImage[3];

		for (int i = 0; i < imgs.length; i++) {
			imgs[i] = atlas.getSubimage(i * VOLUME_DEFAULT_WIDTH, 0, VOLUME_DEFAULT_WIDTH, VOLUME_DEFAULT_HEIGHT);
		}

		slider = atlas.getSubimage(3 * VOLUME_DEFAULT_WIDTH, 0, SLIDER_DEFAULT_WIDTH, VOLUME_DEFAULT_HEIGHT);
	}

	public void update() {
		index = mousePressed ? 2 : (mouseOver ? 1 : 0);
	}

	public void draw(Graphics g) {
		g.drawImage(slider, x, y, width, height, null);
		g.drawImage(imgs[index], buttonX - VOLUME_WIDTH / 2, y, VOLUME_WIDTH, height, null);
	}

	public void changeX(int x) {
		if (x < minX) {
			buttonX = minX;
		} else if (x > maxX) {
			buttonX = maxX;
		} else {
			buttonX = x;
		}

		updateFloatValue();
		bounds.x = buttonX - VOLUME_WIDTH / 2;
	}

	private void updateFloatValue() {
		float range = maxX - minX;
		float offset = buttonX - minX;
		floatValue = offset / range;
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

	public float getFloatValue() {
		return floatValue;
	}
}
