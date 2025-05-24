package ui;

import static utilz.Constants.UI.URMButtons.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

public class LevelCompletedOverlay {

	private Playing playing;
	private UrmButton menu, next;
	private BufferedImage img;
	private int bgX, bgY, bgW, bgH;

	public LevelCompletedOverlay(Playing playing) {
		this.playing = playing;
		loadImage();
		initButtons();
	}

	private void loadImage() {
		img = LoadSave.GetSpriteAtlas(LoadSave.COMPLETED_IMG);
		bgW = (int) (img.getWidth() * Game.SCALE);
		bgH = (int) (img.getHeight() * Game.SCALE);
		bgX = (Game.GAME_WIDTH - bgW) / 2;
		bgY = (int) (75 * Game.SCALE);
	}

	private void initButtons() {
		int y = (int) (195 * Game.SCALE);
		int menuX = (int) (330 * Game.SCALE);
		int nextX = (int) (445 * Game.SCALE);

		menu = new UrmButton(menuX, y, URM_SIZE, URM_SIZE, 2);
		next = new UrmButton(nextX, y, URM_SIZE, URM_SIZE, 0);
	}

	public void draw(Graphics g) {
		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

		g.drawImage(img, bgX, bgY, bgW, bgH, null);
		menu.draw(g);
		next.draw(g);
	}

	public void update() {
		menu.update();
		next.update();
	}

	public void mousePressed(MouseEvent e) {
		if (isIn(menu, e)) {
			menu.setMousePressed(true);
			return;
		}
		if (isIn(next, e)) {
			next.setMousePressed(true);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (isIn(menu, e) && menu.isMousePressed()) {
			playing.resetAll();
			playing.setGamestate(Gamestate.MENU);
		} else if (isIn(next, e) && next.isMousePressed()) {
			playing.loadNextLevel();
			playing.getGame().getAudioPlayer().setLevelSong(
				playing.getLevelManager().getLevelIndex());
		}

		menu.resetBools();
		next.resetBools();
	}

	public void mouseMoved(MouseEvent e) {
		menu.setMouseOver(false);
		next.setMouseOver(false);

		if (isIn(menu, e)) {
			menu.setMouseOver(true);
		} else if (isIn(next, e)) {
			next.setMouseOver(true);
		}
	}

	private boolean isIn(UrmButton button, MouseEvent e) {
		return button.getBounds().contains(e.getX(), e.getY());
	}
}
