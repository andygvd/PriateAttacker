package entities;

import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.Directions.*;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import main.Game;

public class Crabby extends Enemy {

	private int attackBoxOffsetX;

	public Crabby(float x, float y) {
		super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
		initHitbox(22, 19);
		initAttackBox();
	}

	private void initAttackBox() {
		float width = 82 * Game.SCALE;
		float height = 19 * Game.SCALE;
		attackBox = new Rectangle2D.Float(x, y, width, height);
		attackBoxOffsetX = (int) (Game.SCALE * 30);
	}

	public void update(int[][] lvlData, Player player) {
		updateBehavior(lvlData, player);
		updateAnimationTick();
		updateAttackBox();
	}

	private void updateAttackBox() {
		if (attackBox != null) {
			attackBox.x = hitbox.x - attackBoxOffsetX;
			attackBox.y = hitbox.y;
		}
	}

	private void updateBehavior(int[][] lvlData, Player player) {
		if (firstUpdate) {
			firstUpdateCheck(lvlData);
		}

		if (inAir) {
			updateInAir(lvlData);
			return;
		}

		switch (state) {
			case IDLE:
				newState(RUNNING);
				break;

			case RUNNING:
				if (canSeePlayer(lvlData, player)) {
					turnTowardsPlayer(player);
					if (isPlayerCloseForAttack(player)) {
						newState(ATTACK);
					}
				}
				move(lvlData);
				break;

			case ATTACK:
				if (aniIndex == 0) attackChecked = false;
				if (aniIndex == 3 && !attackChecked) {
					checkPlayerHit(attackBox, player);
				}
				break;

			case HIT:
				// Future hit response logic
				break;
		}
	}

	public int flipX() {
		return (walkDir == RIGHT) ? width : 0;
	}

	public int flipW() {
		return (walkDir == RIGHT) ? -1 : 1;
	}
}
