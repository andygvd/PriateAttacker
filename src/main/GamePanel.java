package main;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

import inputs.KeyboardInputs;
import inputs.MouseInputs;

import static main.Game.GAME_WIDTH;
import static main.Game.GAME_HEIGHT;

public class GamePanel extends JPanel {

	private MouseInputs mouseInputs;
	private Game game;

	public GamePanel(Game game) {
		this.game = game;
		this.mouseInputs = new MouseInputs(this);

		configurePanel();
		initializeInputs();
	}

	private void configurePanel() {
		Dimension screenSize = new Dimension(GAME_WIDTH, GAME_HEIGHT);
		setPreferredSize(screenSize);
	}

	private void initializeInputs() {
		addKeyListener(new KeyboardInputs(this));
		addMouseListener(mouseInputs);
		addMouseMotionListener(mouseInputs);
	}

	public void updateGame() {
		// Reserved for future logic
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		game.render(g);
	}

	public Game getGame() {
		return game;
	}
}
