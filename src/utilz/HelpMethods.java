package utilz;

import static utilz.Constants.EnemyConstants.CRABBY;
import static utilz.Constants.ObjectConstants.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Crabby;
import main.Game;
import objects.*;

public class HelpMethods {

	public static boolean CanMoveHere(float x, float y, float w, float h, int[][] lvlData) {
		return !IsSolid(x, y, lvlData)
		    && !IsSolid(x + w, y, lvlData)
		    && !IsSolid(x, y + h, lvlData)
		    && !IsSolid(x + w, y + h, lvlData);
	}

	private static boolean IsSolid(float x, float y, int[][] lvlData) {
		int maxWidth = lvlData[0].length * Game.TILES_SIZE;
		if (x < 0 || x >= maxWidth || y < 0 || y >= Game.GAME_HEIGHT)
			return true;

		int tileX = (int) (x / Game.TILES_SIZE);
		int tileY = (int) (y / Game.TILES_SIZE);
		return IsTileSolid(tileX, tileY, lvlData);
	}

	public static boolean IsTileSolid(int x, int y, int[][] lvlData) {
		int val = lvlData[y][x];
		return val >= 48 || val < 0 || val != 11;
	}

	public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
		return IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData) &&
		       IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData);
	}

	public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
		float testX = xSpeed > 0 ? hitbox.x + hitbox.width + xSpeed : hitbox.x + xSpeed;
		return IsSolid(testX, hitbox.y + hitbox.height + 1, lvlData);
	}

	public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
		int tileX = (int) (hitbox.x / Game.TILES_SIZE);
		if (xSpeed > 0) {
			int tileEdgeX = tileX * Game.TILES_SIZE;
			return tileEdgeX + Game.TILES_SIZE - (int) hitbox.width - 1;
		}
		return tileX * Game.TILES_SIZE;
	}

	public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
		int tileY = (int) (hitbox.y / Game.TILES_SIZE);
		if (airSpeed > 0) {
			return tileY * Game.TILES_SIZE + Game.TILES_SIZE - (int) hitbox.height - 1;
		}
		return tileY * Game.TILES_SIZE;
	}

	public static boolean IsProjectileHittingLevel(Projectile p, int[][] lvlData) {
		float x = p.getHitbox().x + p.getHitbox().width / 2;
		float y = p.getHitbox().y + p.getHitbox().height / 2;
		return IsSolid(x, y, lvlData);
	}

	public static boolean CanCannonSeePlayer(int[][] lvlData, Rectangle2D.Float cannon, Rectangle2D.Float player, int row) {
		int cannonTileX = (int) (cannon.x / Game.TILES_SIZE);
		int playerTileX = (int) (player.x / Game.TILES_SIZE);
		return IsAllTilesClear(Math.min(cannonTileX, playerTileX), Math.max(cannonTileX, playerTileX), row, lvlData);
	}

	public static boolean IsAllTilesClear(int xStart, int xEnd, int y, int[][] lvlData) {
		for (int x = xStart; x < xEnd; x++)
			if (IsTileSolid(x, y, lvlData))
				return false;
		return true;
	}

	public static boolean IsAllTilesWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
		if (!IsAllTilesClear(xStart, xEnd, y, lvlData))
			return false;

		for (int x = xStart; x < xEnd; x++)
			if (!IsTileSolid(x, y + 1, lvlData))
				return false;

		return true;
	}

	public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float from, Rectangle2D.Float to, int row) {
		int xStart = (int) (from.x / Game.TILES_SIZE);
		int xEnd = (int) (to.x / Game.TILES_SIZE);
		return IsAllTilesWalkable(Math.min(xStart, xEnd), Math.max(xStart, xEnd), row, lvlData);
	}

	public static int[][] GetLevelData(BufferedImage img) {
		int[][] data = new int[img.getHeight()][img.getWidth()];
		for (int y = 0; y < img.getHeight(); y++)
			for (int x = 0; x < img.getWidth(); x++) {
				int value = new Color(img.getRGB(x, y)).getRed();
				data[y][x] = (value >= 48) ? 0 : value;
			}
		return data;
	}

	// === Entity and Object Parsing Methods ===

	public static Point GetPlayerSpawn(BufferedImage img) {
		for (int y = 0; y < img.getHeight(); y++)
			for (int x = 0; x < img.getWidth(); x++) {
				if (new Color(img.getRGB(x, y)).getGreen() == 100)
					return new Point(x * Game.TILES_SIZE, y * Game.TILES_SIZE);
			}
		return new Point(Game.TILES_SIZE, Game.TILES_SIZE);
	}

	public static ArrayList<Crabby> GetCrabs(BufferedImage img) {
		ArrayList<Crabby> list = new ArrayList<>();
		for (int y = 0; y < img.getHeight(); y++)
			for (int x = 0; x < img.getWidth(); x++) {
				if (new Color(img.getRGB(x, y)).getGreen() == CRABBY)
					list.add(new Crabby(x * Game.TILES_SIZE, y * Game.TILES_SIZE));
			}
		return list;
	}

	public static ArrayList<Potion> GetPotions(BufferedImage img) {
		ArrayList<Potion> list = new ArrayList<>();
		for (int y = 0; y < img.getHeight(); y++)
			for (int x = 0; x < img.getWidth(); x++) {
				int blue = new Color(img.getRGB(x, y)).getBlue();
				if (blue == RED_POTION || blue == BLUE_POTION)
					list.add(new Potion(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blue));
			}
		return list;
	}

	public static ArrayList<GameContainer> GetContainers(BufferedImage img) {
		ArrayList<GameContainer> list = new ArrayList<>();
		for (int y = 0; y < img.getHeight(); y++)
			for (int x = 0; x < img.getWidth(); x++) {
				int blue = new Color(img.getRGB(x, y)).getBlue();
				if (blue == BOX || blue == BARREL)
					list.add(new GameContainer(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blue));
			}
		return list;
	}

	public static ArrayList<Spike> GetSpikes(BufferedImage img) {
		ArrayList<Spike> list = new ArrayList<>();
		for (int y = 0; y < img.getHeight(); y++)
			for (int x = 0; x < img.getWidth(); x++) {
				if (new Color(img.getRGB(x, y)).getBlue() == SPIKE)
					list.add(new Spike(x * Game.TILES_SIZE, y * Game.TILES_SIZE, SPIKE));
			}
		return list;
	}

	public static ArrayList<Cannon> GetCannons(BufferedImage img) {
		ArrayList<Cannon> list = new ArrayList<>();
		for (int y = 0; y < img.getHeight(); y++)
			for (int x = 0; x < img.getWidth(); x++) {
				int blue = new Color(img.getRGB(x, y)).getBlue();
				if (blue == CANNON_LEFT || blue == CANNON_RIGHT)
					list.add(new Cannon(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blue));
			}
		return list;
	}
}
