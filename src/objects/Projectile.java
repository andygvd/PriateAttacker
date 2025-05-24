package objects;

import java.awt.geom.Rectangle2D;
import main.Game;

import static utilz.Constants.Projectiles.*;

public class Projectile {

	private Rectangle2D.Float hitbox;
	private int dir;
	private boolean active = true;

	public Projectile(int x, int y, int dir) {
		this.dir = dir;

		int xOffset = (dir == 1) ? (int) (29 * Game.SCALE) : (int) (-3 * Game.SCALE);
		int yOffset = (int) (5 * Game.SCALE);

		float posX = x + xOffset;
		float posY = y + yOffset;

		hitbox = new Rectangle2D.Float(posX, posY, CANNON_BALL_WIDTH, CANNON_BALL_HEIGHT);
	}

	public void updatePos() {
		hitbox.x += dir * SPEED;
	}

	public void setPos(int x, int y) {
		hitbox.x = x;
		hitbox.y = y;
	}

	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
