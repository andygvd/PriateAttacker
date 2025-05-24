package entities;

import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.*;
import static utilz.Constants.Directions.*;
import static utilz.Constants.*;

import java.awt.geom.Rectangle2D;

import main.Game;

public abstract class Enemy extends Entity {

	protected int enemyType;
	protected boolean firstUpdate = true;
	protected int walkDir = LEFT;
	protected int tileY;
	protected float attackDistance = Game.TILES_SIZE;
	protected boolean active = true;
	protected boolean attackChecked;

	public Enemy(float x, float y, int width, int height, int enemyType) {
		super(x, y, width, height);
		this.enemyType = enemyType;
		this.maxHealth = GetMaxHealth(enemyType);
		this.currentHealth = maxHealth;
		this.walkSpeed = Game.SCALE * 0.35f;
	}

	protected void firstUpdateCheck(int[][] lvlData) {
		if (!IsEntityOnFloor(hitbox, lvlData)) {
			inAir = true;
		}
		firstUpdate = false;
	}

	protected void updateInAir(int[][] lvlData) {
		boolean canFall = CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData);

		if (canFall) {
			hitbox.y += airSpeed;
			airSpeed += GRAVITY;
		} else {
			inAir = false;
			hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
			tileY = (int) (hitbox.y / Game.TILES_SIZE);
		}
	}

	protected void move(int[][] lvlData) {
		float xSpeed = (walkDir == LEFT) ? -walkSpeed : walkSpeed;

		boolean canMove = CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData);
		boolean hasFloor = IsFloor(hitbox, xSpeed, lvlData);

		if (canMove && hasFloor) {
			hitbox.x += xSpeed;
		} else {
			changeWalkDir();
		}
	}

	protected void turnTowardsPlayer(Player player) {
		walkDir = (player.hitbox.x > hitbox.x) ? RIGHT : LEFT;
	}

	protected boolean canSeePlayer(int[][] lvlData, Player player) {
		int playerTileY = (int) (player.getHitbox().y / Game.TILES_SIZE);
		if (playerTileY != tileY) return false;

		if (isPlayerInRange(player) && IsSightClear(lvlData, hitbox, player.hitbox, tileY)) {
			return true;
		}
		return false;
	}

	protected boolean isPlayerInRange(Player player) {
		int distance = (int) Math.abs(player.hitbox.x - hitbox.x);
		return distance <= attackDistance * 5;
	}

	protected boolean isPlayerCloseForAttack(Player player) {
		int distance = (int) Math.abs(player.hitbox.x - hitbox.x);
		return distance <= attackDistance;
	}

	protected void newState(int enemyState) {
		state = enemyState;
		aniTick = 0;
		aniIndex = 0;
	}

	public void hurt(int amount) {
		currentHealth -= amount;
		if (currentHealth <= 0) {
			newState(DEAD);
		} else {
			newState(HIT);
		}
	}

	protected void checkPlayerHit(Rectangle2D.Float attackBox, Player player) {
		if (attackBox.intersects(player.hitbox)) {
			player.changeHealth(-GetEnemyDmg(enemyType));
		}
		attackChecked = true;
	}

	protected void updateAnimationTick() {
		aniTick++;

		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;

			int totalFrames = GetSpriteAmount(enemyType, state);
			if (aniIndex >= totalFrames) {
				aniIndex = 0;

				switch (state) {
					case ATTACK, HIT -> state = IDLE;
					case DEAD -> active = false;
				}
			}
		}
	}

	protected void changeWalkDir() {
		walkDir = (walkDir == LEFT) ? RIGHT : LEFT;
	}

	public void resetEnemy() {
		hitbox.x = x;
		hitbox.y = y;
		firstUpdate = true;
		currentHealth = maxHealth;
		newState(IDLE);
		active = true;
		airSpeed = 0f;
	}

	public boolean isActive() {
		return active;
	}
}
