package objects;

import main.Game;

public class Spike extends GameObject {

	public Spike(int x, int y, int objType) {
		super(x, y, objType);

		int width = 32;
		int height = 16;
		initHitbox(width, height);

		xDrawOffset = 0;
		yDrawOffset = (int) (16 * Game.SCALE);

		// Adjust vertical hitbox position
		hitbox.y += yDrawOffset;
	}
}
