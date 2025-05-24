package objects;

import main.Game;

public class Cannon extends GameObject {

	private int tileY;

	public Cannon(int x, int y, int objType) {
		super(x, y, objType);

		this.tileY = y / Game.TILES_SIZE;
		initHitbox(40, 26);

		// Adjust hitbox for better alignment
		int xOffset = (int) (4 * Game.SCALE);
		int yOffset = (int) (6 * Game.SCALE);

		hitbox.x -= xOffset;
		hitbox.y += yOffset;
	}

	public void update() {
		if (doAnimation) {
			updateAnimationTick();
		}
	}

	public int getTileY() {
		return tileY;
	}
}
