package ui;

import static utilz.Constants.UI.URMButtons.URM_SIZE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

public class GameOverOverlay {

	private Playing playing;
	private BufferedImage img;
	private int imgX, imgY, imgW, imgH;

	private UrmButton menu, play;

	public GameOverOverlay(Playing playing) {
		this.playing = playing;
		loadImage();
		initButtons();
	}

	private void initButtons() {
		int y = (int) (195 * Game.SCALE);
		int menuX = (int) (335 * Game.SCALE);
		int playX = (int) (440 * Game.SCALE);

		menu = new UrmButton(menuX, y, URM_SIZE, URM_SIZE, 2);
		play = new UrmButton(playX, y, URM_SIZE, URM_SIZE, 0);
	}

	private void loadImage() {
		img = LoadSave.GetSpriteAtlas(LoadSave.DEATH_SCREEN);
		imgW = (int) (img.getWidth() * Game.SCALE);
		imgH = (int) (img.getHeight() * Game.SCALE);
		imgX = (Game.GAME_WIDTH - imgW) / 2;
		imgY = (int) (100 * Game.SCALE);
	}

	public void draw(Graphics g) {
		// Dim screen
		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

		// Draw overlay
		g.drawImage(img, imgX, imgY, imgW, imgH, null);
		menu.draw(g);
		play.draw(g);
	}

	public void update() {
		menu.update();
		play.update();
	}

	public void keyPressed(KeyEvent e) {
		// No key actions defined
	}

	public void mouseMoved(MouseEvent e) {
		menu.setMouseOver(false);
		play.setMouseOver(false);

		if (isIn(menu, e)) {
			menu.setMouseOver(true);
		} else if (isIn(play, e)) {
			play.setMouseOver(true);
		}
	}

	public void mousePressed(MouseEvent e) {
		if (isIn(menu, e)) {
			menu.setMousePressed(true);
			return;
		}
		if (isIn(play, e)) {
			play.setMousePressed(true);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (isIn(menu, e) && menu.isMousePressed()) {
			playing.resetAll();
			playing.setGamestate(Gamestate.MENU);
		} else if (isIn(play, e) && play.isMousePressed()) {
			playing.resetAll();
			playing.getGame().getAudioPlayer()
				.setLevelSong(playing.getLevelManager().getLevelIndex());
		}

		menu.resetBools();
		play.resetBools();
	}

	private boolean isIn(UrmButton button, MouseEvent e) {
		return button.getBounds().contains(e.getX(), e.getY());
	}
}
