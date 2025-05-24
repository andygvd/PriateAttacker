package utilz;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;

public class LoadSave {

	// === File Name Constants ===
	public static final String PLAYER_ATLAS       = "player_sprites.png";
	public static final String LEVEL_ATLAS        = "outside_sprites.png";
	public static final String MENU_BUTTONS       = "button_atlas.png";
	public static final String MENU_BACKGROUND    = "menu_background.png";
	public static final String PAUSE_BACKGROUND   = "pause_menu.png";
	public static final String SOUND_BUTTONS      = "sound_button.png";
	public static final String URM_BUTTONS        = "urm_buttons.png";
	public static final String VOLUME_BUTTONS     = "volume_buttons.png";
	public static final String MENU_BACKGROUND_IMG = "background_menu.png";
	public static final String PLAYING_BG_IMG     = "playing_bg_img.png";
	public static final String BIG_CLOUDS         = "big_clouds.png";
	public static final String SMALL_CLOUDS       = "small_clouds.png";
	public static final String CRABBY_SPRITE      = "crabby_sprite.png";
	public static final String STATUS_BAR         = "health_power_bar.png";
	public static final String COMPLETED_IMG      = "completed_sprite.png";
	public static final String POTION_ATLAS       = "potions_sprites.png";
	public static final String CONTAINER_ATLAS    = "objects_sprites.png";
	public static final String TRAP_ATLAS         = "trap_atlas.png";
	public static final String CANNON_ATLAS       = "cannon_atlas.png";
	public static final String CANNON_BALL        = "ball.png";
	public static final String DEATH_SCREEN       = "death_screen.png";
	public static final String OPTIONS_MENU       = "options_background.png";

	// === Load Single Sprite ===
	public static BufferedImage GetSpriteAtlas(String fileName) {
		BufferedImage img = null;
		InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);

		try {
			img = ImageIO.read(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return img;
	}

	// === Load Level Images from /lvls Folder ===
	public static BufferedImage[] GetAllLevels() {
		URL url = LoadSave.class.getResource("/lvls");
		File folder = null;

		try {
			folder = new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		File[] unsorted = folder.listFiles();
		File[] sorted = new File[unsorted.length];

		for (int i = 0; i < sorted.length; i++) {
			String expectedName = (i + 1) + ".png";
			for (File file : unsorted) {
				if (file.getName().equals(expectedName)) {
					sorted[i] = file;
					break;
				}
			}
		}

		BufferedImage[] images = new BufferedImage[sorted.length];

		for (int i = 0; i < images.length; i++) {
			try {
				images[i] = ImageIO.read(sorted[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return images;
	}
}
