package objects;

import main.Game;

public class Potion extends GameObject {

	private float hoverOffset = 0f;
	private int maxHoverOffset;
	private int hoverDir = 1;

	public Potion(int x, int y, int objType) {
		super(x, y, objType);
		this.doAnimation = true;

		initHitbox(7, 14);

		xDrawOffset = (int) (3 * Game.SCALE);
		yDrawOffset = (int) (2 * Game.SCALE);
		maxHoverOffset = (int) (10 * Game.SCALE);
	}

	public void update() {
		updateAnimationTick();
		updateHover();
	}

	private void updateHover() {
		float hoverStep = 0.075f * Game.SCALE * hoverDir;
		hoverOffset += hoverStep;

		if (hoverOffset >= maxHoverOffset) {
			hoverDir = -1;
		} else if (hoverOffset < 0) {
			hoverDir = 1;
		}

		hitbox.y = y + hoverOffset;
	}
}
