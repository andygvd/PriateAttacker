package main;

import java.awt.Graphics;

import audio.AudioPlayer;
import gamestates.GameOptions;
import gamestates.Gamestate;
import gamestates.Menu;
import gamestates.Playing;
import ui.AudioOptions;
import utilz.LoadSave;

public class Game implements Runnable {

	private GameWindow gameWindow;
	private GamePanel gamePanel;
	private Thread gameThread;

	private final int FPS_SET = 120;
	private final int UPS_SET = 200;

	private Playing playing;
	private Menu menu;
	private GameOptions gameOptions;
	private AudioOptions audioOptions;
	private AudioPlayer audioPlayer;

	public static final int TILES_DEFAULT_SIZE = 32;
	public static final float SCALE = 2f;
	public static final int TILES_IN_WIDTH = 26;
	public static final int TILES_IN_HEIGHT = 14;
	public static final int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
	public static final int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
	public static final int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

	public Game() {
		initClasses();

		gamePanel = new GamePanel(this);
		gameWindow = new GameWindow(gamePanel);

		gamePanel.setFocusable(true);
		gamePanel.requestFocusInWindow();

		startGameLoop();
	}

	private void initClasses() {
		audioOptions = new AudioOptions(this);
		audioPlayer = new AudioPlayer();
		menu = new Menu(this);
		playing = new Playing(this);
		gameOptions = new GameOptions(this);
	}

	private void startGameLoop() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	public void update() {
		switch (Gamestate.state) {
			case MENU -> menu.update();
			case PLAYING -> playing.update();
			case OPTIONS -> gameOptions.update();
			case QUIT -> System.exit(0);
			default -> System.exit(0);
		}
	}

	public void render(Graphics g) {
		switch (Gamestate.state) {
			case MENU -> menu.draw(g);
			case PLAYING -> playing.draw(g);
			case OPTIONS -> gameOptions.draw(g);
			default -> {}
		}
	}

	@Override
	public void run() {
		final double timePerFrame = 1_000_000_000.0 / FPS_SET;
		final double timePerUpdate = 1_000_000_000.0 / UPS_SET;

		long previousTime = System.nanoTime();
		long lastCheck = System.currentTimeMillis();

		int frames = 0;
		int updates = 0;

		double deltaU = 0;
		double deltaF = 0;

		while (true) {
			long currentTime = System.nanoTime();

			deltaU += (currentTime - previousTime) / timePerUpdate;
			deltaF += (currentTime - previousTime) / timePerFrame;
			previousTime = currentTime;

			if (deltaU >= 1) {
				update();
				updates++;
				deltaU--;
			}

			if (deltaF >= 1) {
				gamePanel.repaint();
				frames++;
				deltaF--;
			}

			if (System.currentTimeMillis() - lastCheck >= 1000) {
				lastCheck = System.currentTimeMillis();
				System.out.println("FPS: " + frames + " | UPS: " + updates);
				frames = 0;
				updates = 0;
			}
		}
	}

	public void windowFocusLost() {
		if (Gamestate.state == Gamestate.PLAYING) {
			playing.getPlayer().resetDirBooleans();
		}
	}

	public Menu getMenu() {
		return menu;
	}

	public Playing getPlaying() {
		return playing;
	}

	public GameOptions getGameOptions() {
		return gameOptions;
	}

	public AudioOptions getAudioOptions() {
		return audioOptions;
	}

	public AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}
}
