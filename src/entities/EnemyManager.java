package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.Playing;
import levels.Level;
import utilz.LoadSave;
import static utilz.Constants.EnemyConstants.*;

public class EnemyManager {

	private Playing playing;
	private BufferedImage[][] crabbyArr;
	private ArrayList<Crabby> crabbies = new ArrayList<>();

	public EnemyManager(Playing playing) {
		this.playing = playing;
		loadEnemyImgs();
	}

	public void loadEnemies(Level level) {
		crabbies = level.getCrabs();
	}

	public void update(int[][] lvlData, Player player) {
		boolean activeEnemyFound = false;

		for (Crabby crab : crabbies) {
			if (!crab.isActive())
				continue;

			crab.update(lvlData, player);
			activeEnemyFound = true;
		}

		if (!activeEnemyFound)
			playing.setLevelCompleted(true);
	}

	public void draw(Graphics g, int xLvlOffset) {
		drawCrabs(g, xLvlOffset);
	}

	private void drawCrabs(Graphics g, int xLvlOffset) {
		for (Crabby crab : crabbies) {
			if (!crab.isActive())
				continue;

			int x = (int) crab.getHitbox().x - xLvlOffset - CRABBY_DRAWOFFSET_X + crab.flipX();
			int y = (int) crab.getHitbox().y - CRABBY_DRAWOFFSET_Y;
			int w = CRABBY_WIDTH * crab.flipW();

			g.drawImage(
				crabbyArr[crab.getState()][crab.getAniIndex()],
				x, y, w, CRABBY_HEIGHT,
				null
			);
		}
	}

	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		for (Crabby crab : crabbies) {
			if (!crab.isActive() || crab.getCurrentHealth() <= 0)
				continue;

			if (attackBox.intersects(crab.getHitbox())) {
				crab.hurt(10);
				return;
			}
		}
	}

	private void loadEnemyImgs() {
		crabbyArr = new BufferedImage[5][9];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE);

		for (int row = 0; row < crabbyArr.length; row++) {
			for (int col = 0; col < crabbyArr[row].length; col++) {
				int x = col * CRABBY_WIDTH_DEFAULT;
				int y = row * CRABBY_HEIGHT_DEFAULT;
				crabbyArr[row][col] = temp.getSubimage(x, y, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
			}
		}
	}

	public void resetAllEnemies() {
		for (Crabby crab : crabbies) {
			crab.resetEnemy();
		}
	}
}
