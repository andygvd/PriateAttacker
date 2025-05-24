package ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import utilz.LoadSave;

import static utilz.Constants.UI.Buttons.*;

public class MenuButton {

	private int xPos, yPos;
	private int rowIndex;
	private int index = 0;

	private final int xOffsetCenter = B_WIDTH / 2;

	private Gamestate state;
	private BufferedImage[] imgs;

	private boolean mouseOver = false;
	private boolean mousePressed = false;

	private Rectangle bounds;

	public MenuButton(int xPos, int yPos, int rowIndex, Gamestate state) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.rowIndex = rowIndex;
		this.state = state;

		loadImages();
		initBounds();
	}

	private void loadImages() {
		imgs = new BufferedImage[3];
		BufferedImage atlas = LoadSave.GetSpriteAtlas(LoadSave.MENU_BUTTONS);

		for (int i = 0; i < imgs.length; i++) {
			imgs[i] = atlas.getSubimage(
				i * B_WIDTH_DEFAULT,
				rowIndex * B_HEIGHT_DEFAULT,
				B_WIDTH_DEFAULT,
				B_HEIGHT_DEFAULT
			);
		}
	}

	private void initBounds() {
		int boundX = xPos - xOffsetCenter;
		bounds = new Rectangle(boundX, yPos, B_WIDTH, B_HEIGHT);
	}

	public void draw(Graphics g) {
		int drawX = xPos - xOffsetCenter;
		g.drawImage(imgs[index], drawX, yPos, B_WIDTH, B_HEIGHT, null);
	}

	public void update() {
		index = mousePressed ? 2 : (mouseOver ? 1 : 0);
	}

	public void applyGamestate() {
		Gamestate.state = state;
	}

	public void resetBools() {
		mouseOver = false;
		mousePressed = false;
	}

	// === Getters and Setters ===

	public Rectangle getBounds() {
		return bounds;
	}

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

	public Gamestate getState() {
		return state;
	}
}
