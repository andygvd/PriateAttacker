package objects;

import static utilz.Constants.ANI_SPEED;
import static utilz.Constants.ObjectConstants.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import main.Game;

public class GameObject {

	protected int x, y, objType;
	protected Rectangle2D.Float hitbox;
	protected boolean doAnimation = false, active = true;
	protected int aniTick = 0, aniIndex = 0;
	protected int xDrawOffset = 0, yDrawOffset = 0;

	public GameObject(int x, int y, int objType) {
		this.x = x;
		this.y = y;
		this.objType = objType;
	}

	protected void updateAnimationTick() {
		aniTick++;
		if (aniTick < ANI_SPEED) return;

		aniTick = 0;
		aniIndex++;

		if (aniIndex >= GetSpriteAmount(objType)) {
			aniIndex = 0;

			boolean isBreakable = objType == BARREL || objType == BOX;
			boolean isCannon = objType == CANNON_LEFT || objType == CANNON_RIGHT;

			if (isBreakable) {
				doAnimation = false;
				active = false;
			} else if (isCannon) {
				doAnimation = false;
			}
		}
	}

	public void reset() {
		aniIndex = 0;
		aniTick = 0;
		active = true;

		boolean animatable = !(objType == BARREL || objType == BOX || objType == CANNON_LEFT || objType == CANNON_RIGHT);
		doAnimation = animatable;
	}

	protected void initHitbox(int width, int height) {
		hitbox = new Rectangle2D.Float(
			x,
			y,
			(int) (width * Game.SCALE),
			(int) (height * Game.SCALE)
		);
	}

	public void drawHitbox(Graphics g, int xLvlOffset) {
		g.setColor(Color.PINK);
		int drawX = (int) hitbox.x - xLvlOffset;
		int drawY = (int) hitbox.y;
		int drawW = (int) hitbox.width;
		int drawH = (int) hitbox.height;

		g.drawRect(drawX, drawY, drawW, drawH);
	}

	// === Getters and Setters ===

	public int getObjType() {
		return objType;
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

	public void setAnimation(boolean doAnimation) {
		this.doAnimation = doAnimation;
	}

	public int getxDrawOffset() {
		return xDrawOffset;
	}

	public int getyDrawOffset() {
		return yDrawOffset;
	}

	public int getAniIndex() {
		return aniIndex;
	}

	public int getAniTick() {
		return aniTick;
	}
}
