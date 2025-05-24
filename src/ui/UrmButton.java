package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import utilz.LoadSave;
import static utilz.Constants.UI.URMButtons.*;

public class UrmButton extends PauseButton {

	private BufferedImage[] imgs;
	private int rowIndex;
	private int index = 0;

	private boolean mouseOver = false;
	private boolean mousePressed = false;

	public UrmButton(int x, int y, int width, int height, int rowIndex) {
		super(x, y, width, height);
		this.rowIndex = rowIndex;
		loadImages();
	}

	private void loadImages() {
		BufferedImage atlas = LoadSave.GetSpriteAtlas(LoadSave.URM_BUTTONS);
		imgs = new BufferedImage[3];

		for (int i = 0; i < imgs.length; i++) {
			int x = i * URM_DEFAULT_SIZE;
			int y = rowIndex * URM_DEFAULT_SIZE;
			imgs[i] = atlas.getSubimage(x, y, URM_DEFAULT_SIZE, URM_DEFAULT_SIZE);
		}
	}

	public void update() {
		index = mousePressed ? 2 : (mouseOver ? 1 : 0);
	}

	public void draw(Graphics g) {
		g.drawImage(imgs[index], x, y, URM_SIZE, URM_SIZE, null);
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
}
