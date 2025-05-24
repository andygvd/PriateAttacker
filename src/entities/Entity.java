package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import main.Game;

public abstract class Entity {

	protected float x, y;
	protected int width, height;
	protected Rectangle2D.Float hitbox;
	protected int aniTick = 0, aniIndex = 0;
	protected int state = 0;
	protected float airSpeed = 0f;
	protected boolean inAir = false;
	protected int maxHealth = 0;
	protected int currentHealth = 0;
	protected Rectangle2D.Float attackBox;
	protected float walkSpeed = 0f;

	public Entity(float x, float y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	protected void drawAttackBox(Graphics g, int xLvlOffset) {
		if (attackBox == null) return;
		int drawX = (int) (attackBox.x - xLvlOffset);
		int drawY = (int) attackBox.y;
		int drawW = (int) attackBox.width;
		int drawH = (int) attackBox.height;

		g.setColor(Color.RED);
		g.drawRect(drawX, drawY, drawW, drawH);
	}

	protected void drawHitbox(Graphics g, int xLvlOffset) {
		if (hitbox == null) return;
		int drawX = (int) (hitbox.x - xLvlOffset);
		int drawY = (int) hitbox.y;
		int drawW = (int) hitbox.width;
		int drawH = (int) hitbox.height;

		g.setColor(Color.PINK);
		g.drawRect(drawX, drawY, drawW, drawH);
	}

	protected void initHitbox(int width, int height) {
		float hbWidth = width * Game.SCALE;
		float hbHeight = height * Game.SCALE;
		hitbox = new Rectangle2D.Float(x, y, hbWidth, hbHeight);
	}

	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}

	public int getState() {
		return state;
	}

	public int getAniIndex() {
		return aniIndex;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}
}
