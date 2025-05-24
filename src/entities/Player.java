package entities;

import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;
import static utilz.Constants.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import audio.AudioPlayer;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

public class Player extends Entity {

	private BufferedImage[][] animations;
	private boolean moving = false, attacking = false;
	private boolean left, right, jump;
	private int[][] lvlData;

	private float xDrawOffset = 21 * Game.SCALE;
	private float yDrawOffset = 4 * Game.SCALE;

	private float jumpSpeed = -2.25f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE;

	private BufferedImage statusBarImg;

	private final int statusBarWidth = (int) (192 * Game.SCALE);
	private final int statusBarHeight = (int) (58 * Game.SCALE);
	private final int statusBarX = (int) (10 * Game.SCALE);
	private final int statusBarY = (int) (10 * Game.SCALE);

	private final int healthBarWidth = (int) (150 * Game.SCALE);
	private final int healthBarHeight = (int) (4 * Game.SCALE);
	private final int healthBarXStart = (int) (34 * Game.SCALE);
	private final int healthBarYStart = (int) (14 * Game.SCALE);
	private int healthWidth = healthBarWidth;

	private final int powerBarWidth = (int) (104 * Game.SCALE);
	private final int powerBarHeight = (int) (2 * Game.SCALE);
	private final int powerBarXStart = (int) (44 * Game.SCALE);
	private final int powerBarYStart = (int) (34 * Game.SCALE);
	private int powerWidth = powerBarWidth;
	private final int powerMaxValue = 200;
	private int powerValue = powerMaxValue;

	private int flipX = 0;
	private int flipW = 1;

	private boolean attackChecked;
	private Playing playing;
	private int tileY = 0;

	private boolean powerAttackActive;
	private int powerAttackTick = 0;
	private int powerGrowSpeed = 15;
	private int powerGrowTick = 0;

	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
		this.state = IDLE;
		this.maxHealth = 100;
		this.currentHealth = 35;
		this.walkSpeed = Game.SCALE * 1.0f;

		loadAnimations();
		initHitbox(20, 27);
		initAttackBox();
	}

	public void setSpawn(Point spawn) {
		this.x = spawn.x;
		this.y = spawn.y;
		hitbox.x = x;
		hitbox.y = y;
	}

	private void initAttackBox() {
		int boxSize = (int) (20 * Game.SCALE);
		attackBox = new Rectangle2D.Float(x, y, boxSize, boxSize);
		resetAttackBox();
	}

	public void update() {
		updateHealthBar();
		updatePowerBar();

		if (currentHealth <= 0) {
			if (state != DEAD) {
				state = DEAD;
				aniTick = 0;
				aniIndex = 0;
				playing.setPlayerDying(true);
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
			} else if (aniIndex == GetSpriteAmount(DEAD) - 1 && aniTick >= ANI_SPEED - 1) {
				playing.setGameOver(true);
				playing.getGame().getAudioPlayer().stopSong();
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAMEOVER);
			} else {
				updateAnimationTick();
			}
			return;
		}

		updateAttackBox();
		updatePos();

		if (moving) {
			checkPotionTouched();
			checkSpikesTouched();
			tileY = (int) (hitbox.y / Game.TILES_SIZE);

			if (powerAttackActive) {
				powerAttackTick++;
				if (powerAttackTick >= 35) {
					powerAttackTick = 0;
					powerAttackActive = false;
				}
			}
		}

		if (attacking || powerAttackActive) {
			checkAttack();
		}

		updateAnimationTick();
		setAnimation();
	}

	private void updatePos() {
		moving = false;

		if (jump) jump();

		if (!inAir && !powerAttackActive && ((!left && !right) || (left && right))) return;

		float xSpeed = 0;

		if (left && !right) {
			xSpeed = -walkSpeed;
			flipX = width;
			flipW = -1;
		} else if (right && !left) {
			xSpeed = walkSpeed;
			flipX = 0;
			flipW = 1;
		}

		if (powerAttackActive && ((left && right) || (!left && !right))) {
			xSpeed = (flipW == -1 ? -walkSpeed : walkSpeed);
		}

		if (powerAttackActive) xSpeed *= 3;

		if (!inAir && !IsEntityOnFloor(hitbox, lvlData)) {
			inAir = true;
		}

		if (inAir && !powerAttackActive) {
			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				airSpeed += GRAVITY;
				updateXPos(xSpeed);
			} else {
				hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if (airSpeed > 0)
					resetInAir();
				else
					airSpeed = fallSpeedAfterCollision;
				updateXPos(xSpeed);
			}
		} else {
			updateXPos(xSpeed);
		}

		moving = true;
	}

	private void updateXPos(float xSpeed) {
		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
			hitbox.x += xSpeed;
		} else {
			hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
			if (powerAttackActive) {
				powerAttackActive = false;
				powerAttackTick = 0;
			}
		}
	}

	private void jump() {
		if (inAir) return;
		playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
		inAir = true;
		airSpeed = jumpSpeed;
	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;
	}

	private void checkAttack() {
		if (attackChecked || aniIndex != 1) return;

		attackChecked = true;

		if (powerAttackActive) attackChecked = false;

		playing.checkEnemyHit(attackBox);
		playing.checkObjectHit(attackBox);
		playing.getGame().getAudioPlayer().playAttackSound();
	}

	private void updateAttackBox() {
		float offset = Game.SCALE * 10;
		if (right && left) {
			attackBox.x = (flipW == 1) ? hitbox.x + hitbox.width + offset : hitbox.x - hitbox.width - offset;
		} else if (right || (powerAttackActive && flipW == 1)) {
			attackBox.x = hitbox.x + hitbox.width + offset;
		} else if (left || (powerAttackActive && flipW == -1)) {
			attackBox.x = hitbox.x - hitbox.width - offset;
		}
		attackBox.y = hitbox.y + offset;
	}

	private void updateHealthBar() {
		float ratio = currentHealth / (float) maxHealth;
		healthWidth = (int) (ratio * healthBarWidth);
	}

	private void updatePowerBar() {
		float ratio = powerValue / (float) powerMaxValue;
		powerWidth = (int) (ratio * powerBarWidth);

		powerGrowTick++;
		if (powerGrowTick >= powerGrowSpeed) {
			powerGrowTick = 0;
			changePower(1);
		}
	}

	private void setAnimation() {
		int prevState = state;

		state = moving ? RUNNING : IDLE;

		if (inAir) state = (airSpeed < 0) ? JUMP : FALLING;

		if (powerAttackActive || attacking) {
			state = ATTACK;
			if (prevState != ATTACK) {
				aniIndex = 1;
				aniTick = 0;
			}
			if (powerAttackActive) return;
		}

		if (prevState != state) resetAniTick();
	}

	private void updateAnimationTick() {
		aniTick++;
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(state)) {
				aniIndex = 0;
				attacking = false;
				attackChecked = false;
			}
		}
	}

	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;
	}

	private void loadAnimations() {
		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
		animations = new BufferedImage[7][8];

		for (int row = 0; row < animations.length; row++) {
			for (int col = 0; col < animations[row].length; col++) {
				animations[row][col] = img.getSubimage(col * 64, row * 40, 64, 40);
			}
		}
		statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
	}

	private void checkSpikesTouched() {
		playing.checkSpikesTouched(this);
	}

	private void checkPotionTouched() {
		playing.checkPotionTouched(hitbox);
	}

	private void resetAttackBox() {
		float offset = Game.SCALE * 10;
		attackBox.x = (flipW == 1) ? hitbox.x + hitbox.width + offset : hitbox.x - hitbox.width - offset;
	}

	public void render(Graphics g, int lvlOffset) {
		g.drawImage(
			animations[state][aniIndex],
			(int) (hitbox.x - xDrawOffset) - lvlOffset + flipX,
			(int) (hitbox.y - yDrawOffset),
			width * flipW, height, null
		);
		drawUI(g);
	}

	private void drawUI(Graphics g) {
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);

		g.setColor(Color.red);
		g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);

		g.setColor(Color.yellow);
		g.fillRect(powerBarXStart + statusBarX, powerBarYStart + statusBarY, powerWidth, powerBarHeight);
	}

	// Public methods preserved as-is
	public void changeHealth(int value) {
		currentHealth += value;
		if (currentHealth < 0) currentHealth = 0;
		else if (currentHealth > maxHealth) currentHealth = maxHealth;
	}

	public void kill() {
		currentHealth = 0;
	}

	public void changePower(int value) {
		powerValue += value;
		if (powerValue < 0) powerValue = 0;
		else if (powerValue > powerMaxValue) powerValue = powerMaxValue;
	}

	public void loadLvlData(int[][] lvlData) {
		this.lvlData = lvlData;
		if (!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;
	}

	public void resetDirBooleans() {
		left = false;
		right = false;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public void resetAll() {
		resetDirBooleans();
		inAir = false;
		attacking = false;
		moving = false;
		airSpeed = 0f;
		state = IDLE;
		currentHealth = maxHealth;

		hitbox.x = x;
		hitbox.y = y;
		resetAttackBox();

		if (!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;
	}

	public int getTileY() {
		return tileY;
	}

	public void powerAttack() {
		if (!powerAttackActive && powerValue >= 60) {
			powerAttackActive = true;
			changePower(-60);
		}
	}
}
