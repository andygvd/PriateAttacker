package objects;

import static utilz.Constants.ObjectConstants.*;
import main.Game;

public class GameContainer extends GameObject {

	public GameContainer(int x, int y, int objType) {
		super(x, y, objType);
		createHitbox();
	}

	private void createHitbox() {
		int hitboxWidth, hitboxHeight;
		int xOffset, yOffset;

		if (objType == BOX) {
			hitboxWidth = 25;
			hitboxHeight = 18;
			xOffset = (int) (7 * Game.SCALE);
			yOffset = (int) (12 * Game.SCALE);
		} else {
			hitboxWidth = 23;
			hitboxHeight = 25;
			xOffset = (int) (8 * Game.SCALE);
			yOffset = (int) (5 * Game.SCALE);
		}

		initHitbox(hitboxWidth, hitboxHeight);

		xDrawOffset = xOffset;
		yDrawOffset = yOffset;

		hitbox.y += yOffset + (int) (2 * Game.SCALE);
		hitbox.x += xOffset / 2;
	}

	public void update() {
		if (doAnimation) {
			updateAnimationTick();
		}
	}
}
