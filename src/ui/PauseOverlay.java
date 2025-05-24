package ui;

import static utilz.Constants.UI.PauseButtons.*;
import static utilz.Constants.UI.URMButtons.*;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

public class PauseOverlay {

	private Playing playing;
	private AudioOptions audioOptions;

	private BufferedImage backgroundImg;
	private int bgX, bgY, bgW, bgH;

	private UrmButton menuB, replayB, unpauseB;

	public PauseOverlay(Playing playing) {
		this.playing = playing;
		this.audioOptions = playing.getGame().getAudioOptions();

		loadBackground();
		initButtons();
	}

	private void loadBackground() {
		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
		bgW = (int) (backgroundImg.getWidth() * Game.SCALE);
		bgH = (int) (backgroundImg.getHeight() * Game.SCALE);
		bgX = (Game.GAME_WIDTH - bgW) / 2;
		bgY = (int) (25 * Game.SCALE);
	}

	private void initButtons() {
		int y = (int) (325 * Game.SCALE);
		menuB = new UrmButton((int) (313 * Game.SCALE), y, URM_SIZE, URM_SIZE, 2);
		replayB = new UrmButton((int) (387 * Game.SCALE), y, URM_SIZE, URM_SIZE, 1);
		unpauseB = new UrmButton((int) (462 * Game.SCALE), y, URM_SIZE, URM_SIZE, 0);
	}

	public void update() {
		menuB.update();
		replayB.update();
		unpauseB.update();
		audioOptions.update();
	}

	public void draw(Graphics g) {
		g.drawImage(backgroundImg, bgX, bgY, bgW, bgH, null);

		menuB.draw(g);
		replayB.draw(g);
		unpauseB.draw(g);
		audioOptions.draw(g);
	}

	public void mouseDragged(MouseEvent e) {
		audioOptions.mouseDragged(e);
	}

	public void mousePressed(MouseEvent e) {
		if (isIn(e, menuB)) {
			menuB.setMousePressed(true);
			return;
		}
		if (isIn(e, replayB)) {
			replayB.setMousePressed(true);
			return;
		}
		if (isIn(e, unpauseB)) {
			unpauseB.setMousePressed(true);
			return;
		}

		audioOptions.mousePressed(e);
	}

	public void mouseReleased(MouseEvent e) {
		if (isIn(e, menuB) && menuB.isMousePressed()) {
			playing.resetAll();
			playing.setGamestate(Gamestate.MENU);
			playing.unpauseGame();
		} else if (isIn(e, replayB) && replayB.isMousePressed()) {
			playing.resetAll();
			playing.unpauseGame();
		} else if (isIn(e, unpauseB) && unpauseB.isMousePressed()) {
			playing.unpauseGame();
		} else {
			audioOptions.mouseReleased(e);
		}

		menuB.resetBools();
		replayB.resetBools();
		unpauseB.resetBools();
	}

	public void mouseMoved(MouseEvent e) {
		menuB.setMouseOver(false);
		replayB.setMouseOver(false);
		unpauseB.setMouseOver(false);

		if (isIn(e, menuB)) {
			menuB.setMouseOver(true);
		} else if (isIn(e, replayB)) {
			replayB.setMouseOver(true);
		} else if (isIn(e, unpauseB)) {
			unpauseB.setMouseOver(true);
		} else {
			audioOptions.mouseMoved(e);
		}
	}

	private boolean isIn(MouseEvent e, PauseButton button) {
		return button.getBounds().contains(e.getX(), e.getY());
	}
}
