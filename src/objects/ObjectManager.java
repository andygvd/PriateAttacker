package objects;

import static utilz.Constants.ObjectConstants.*;
import static utilz.Constants.Projectiles.*;
import static utilz.HelpMethods.CanCannonSeePlayer;
import static utilz.HelpMethods.IsProjectileHittingLevel;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Player;
import gamestates.Playing;
import levels.Level;
import main.Game;
import utilz.LoadSave;

public class ObjectManager {

	private Playing playing;

	private BufferedImage[][] potionImgs, containerImgs;
	private BufferedImage[] cannonImgs;
	private BufferedImage spikeImg, cannonBallImg;

	private ArrayList<Potion> potions;
	private ArrayList<GameContainer> containers;
	private ArrayList<Spike> spikes;
	private ArrayList<Cannon> cannons;
	private ArrayList<Projectile> projectiles = new ArrayList<>();

	public ObjectManager(Playing playing) {
		this.playing = playing;
		loadImgs();
	}

	public void checkSpikesTouched(Player player) {
		for (Spike spike : spikes) {
			if (spike.getHitbox().intersects(player.getHitbox())) {
				player.kill();
				return;
			}
		}
	}

	public void checkObjectTouched(Rectangle2D.Float hitbox) {
		for (Potion potion : potions) {
			if (potion.isActive() && hitbox.intersects(potion.getHitbox())) {
				potion.setActive(false);
				applyEffectToPlayer(potion);
			}
		}
	}

	public void applyEffectToPlayer(Potion potion) {
		if (potion.getObjType() == RED_POTION) {
			playing.getPlayer().changeHealth(RED_POTION_VALUE);
		} else {
			playing.getPlayer().changePower(BLUE_POTION_VALUE);
		}
	}

	public void checkObjectHit(Rectangle2D.Float attackBox) {
		for (GameContainer container : containers) {
			if (!container.isActive() || container.doAnimation) continue;

			if (container.getHitbox().intersects(attackBox)) {
				container.setAnimation(true);
				int type = (container.getObjType() == BARREL) ? 1 : 0;

				float px = container.getHitbox().x + container.getHitbox().width / 2;
				float py = container.getHitbox().y - container.getHitbox().height / 2;

				potions.add(new Potion((int) px, (int) py, type));
				return;
			}
		}
	}

	public void loadObjects(Level newLevel) {
		potions = new ArrayList<>(newLevel.getPotions());
		containers = new ArrayList<>(newLevel.getContainers());
		spikes = newLevel.getSpikes();
		cannons = newLevel.getCannons();
		projectiles.clear();
	}

	private void loadImgs() {
		BufferedImage potionSheet = LoadSave.GetSpriteAtlas(LoadSave.POTION_ATLAS);
		potionImgs = new BufferedImage[2][7];
		for (int j = 0; j < potionImgs.length; j++)
			for (int i = 0; i < potionImgs[j].length; i++)
				potionImgs[j][i] = potionSheet.getSubimage(i * 12, j * 16, 12, 16);

		BufferedImage containerSheet = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_ATLAS);
		containerImgs = new BufferedImage[2][8];
		for (int j = 0; j < containerImgs.length; j++)
			for (int i = 0; i < containerImgs[j].length; i++)
				containerImgs[j][i] = containerSheet.getSubimage(i * 40, j * 30, 40, 30);

		spikeImg = LoadSave.GetSpriteAtlas(LoadSave.TRAP_ATLAS);

		BufferedImage cannonSheet = LoadSave.GetSpriteAtlas(LoadSave.CANNON_ATLAS);
		cannonImgs = new BufferedImage[7];
		for (int i = 0; i < cannonImgs.length; i++)
			cannonImgs[i] = cannonSheet.getSubimage(i * 40, 0, 40, 26);

		cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL);
	}

	public void update(int[][] lvlData, Player player) {
		for (Potion potion : potions)
			if (potion.isActive()) potion.update();

		for (GameContainer container : containers)
			if (container.isActive()) container.update();

		updateCannons(lvlData, player);
		updateProjectiles(lvlData, player);
	}

	private void updateProjectiles(int[][] lvlData, Player player) {
		for (Projectile p : projectiles) {
			if (!p.isActive()) continue;

			p.updatePos();

			if (p.getHitbox().intersects(player.getHitbox())) {
				player.changeHealth(-25);
				p.setActive(false);
			} else if (IsProjectileHittingLevel(p, lvlData)) {
				p.setActive(false);
			}
		}
	}

	private void updateCannons(int[][] lvlData, Player player) {
		for (Cannon cannon : cannons) {
			if (!cannon.doAnimation) {
				boolean sameRow = cannon.getTileY() == player.getTileY();
				if (sameRow && isPlayerInRange(cannon, player) && isPlayerInfrontOfCannon(cannon, player)) {
					if (CanCannonSeePlayer(lvlData, player.getHitbox(), cannon.getHitbox(), cannon.getTileY())) {
						cannon.setAnimation(true);
					}
				}
			}

			cannon.update();

			if (cannon.getAniIndex() == 4 && cannon.getAniTick() == 0)
				shootCannon(cannon);
		}
	}

	private void shootCannon(Cannon cannon) {
		int direction = (cannon.getObjType() == CANNON_LEFT) ? -1 : 1;
		int x = (int) cannon.getHitbox().x;
		int y = (int) cannon.getHitbox().y;
		projectiles.add(new Projectile(x, y, direction));
	}

	private boolean isPlayerInRange(Cannon cannon, Player player) {
		int dx = (int) Math.abs(player.getHitbox().x - cannon.getHitbox().x);
		return dx <= Game.TILES_SIZE * 5;
	}

	private boolean isPlayerInfrontOfCannon(Cannon cannon, Player player) {
		float playerX = player.getHitbox().x;
		float cannonX = cannon.getHitbox().x;

		if (cannon.getObjType() == CANNON_LEFT && cannonX > playerX) return true;
		if (cannon.getObjType() == CANNON_RIGHT && cannonX < playerX) return true;

		return false;
	}

	public void draw(Graphics g, int xLvlOffset) {
		drawPotions(g, xLvlOffset);
		drawContainers(g, xLvlOffset);
		drawTraps(g, xLvlOffset);
		drawCannons(g, xLvlOffset);
		drawProjectiles(g, xLvlOffset);
	}

	private void drawPotions(Graphics g, int xLvlOffset) {
		for (Potion potion : potions) {
			if (!potion.isActive()) continue;

			int type = (potion.getObjType() == RED_POTION) ? 1 : 0;
			int x = (int) (potion.getHitbox().x - potion.getxDrawOffset() - xLvlOffset);
			int y = (int) (potion.getHitbox().y - potion.getyDrawOffset());

			g.drawImage(potionImgs[type][potion.getAniIndex()], x, y, POTION_WIDTH, POTION_HEIGHT, null);
		}
	}

	private void drawContainers(Graphics g, int xLvlOffset) {
		for (GameContainer container : containers) {
			if (!container.isActive()) continue;

			int type = (container.getObjType() == BARREL) ? 1 : 0;
			int x = (int) (container.getHitbox().x - container.getxDrawOffset() - xLvlOffset);
			int y = (int) (container.getHitbox().y - container.getyDrawOffset());

			g.drawImage(containerImgs[type][container.getAniIndex()], x, y, CONTAINER_WIDTH, CONTAINER_HEIGHT, null);
		}
	}

	private void drawTraps(Graphics g, int xLvlOffset) {
		for (Spike spike : spikes) {
			int x = (int) (spike.getHitbox().x - xLvlOffset);
			int y = (int) (spike.getHitbox().y - spike.getyDrawOffset());

			g.drawImage(spikeImg, x, y, SPIKE_WIDTH, SPIKE_HEIGHT, null);
		}
	}

	private void drawCannons(Graphics g, int xLvlOffset) {
		for (Cannon cannon : cannons) {
			int x = (int) cannon.getHitbox().x - xLvlOffset;
			int width = CANNON_WIDTH;

			if (cannon.getObjType() == CANNON_RIGHT) {
				x += width;
				width *= -1;
			}

			int y = (int) cannon.getHitbox().y;
			g.drawImage(cannonImgs[cannon.getAniIndex()], x, y, width, CANNON_HEIGHT, null);
		}
	}

	private void drawProjectiles(Graphics g, int xLvlOffset) {
		for (Projectile p : projectiles) {
			if (!p.isActive()) continue;

			int x = (int) (p.getHitbox().x - xLvlOffset);
			int y = (int) p.getHitbox().y;

			g.drawImage(cannonBallImg, x, y, CANNON_BALL_WIDTH, CANNON_BALL_HEIGHT, null);
		}
	}

	public void resetAllObjects() {
		loadObjects(playing.getLevelManager().getCurrentLevel());

		for (Potion potion : potions) potion.reset();
		for (GameContainer container : containers) container.reset();
		for (Cannon cannon : cannons) cannon.reset();
	}
}
