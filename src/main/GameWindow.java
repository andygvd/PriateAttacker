package main;

import javax.swing.JFrame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class GameWindow {

	private JFrame jframe;

	public GameWindow(GamePanel gamePanel) {
		jframe = new JFrame();

		setupFrame(gamePanel);
		addFocusListener(gamePanel);
	}

	private void setupFrame(GamePanel gamePanel) {
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setResizable(false);
		jframe.add(gamePanel);
		jframe.pack();
		jframe.setLocationRelativeTo(null);
		jframe.setVisible(true);
	}

	private void addFocusListener(GamePanel gamePanel) {
		jframe.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent e) {
				gamePanel.getGame().windowFocusLost();
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				// Optional: handle regained focus
			}
		});
	}
}
