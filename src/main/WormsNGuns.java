package main;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;

import util.ThreadLoop;

public class WormsNGuns extends ThreadLoop {
	// Container
	public static JFrame frame;
	// Application
	public static WormsNGuns app;
	// Game object
	public static Game game;

	// Display settings
	protected static boolean isFullscreen = false;
	public static final int defaultWid = 1280, defaultHei = 1024;

	// Init defaults
	protected WormsNGuns() {
		// Make game
		game = new Game();
		// Start thread
		setFrameRate(Game.frameRate);
		startThread("Main Game");
	}

	// Fullscreen toggle
	public static void refreshWindow(boolean fullscreen) {
		// End thread
		app.interruptThread();
		// Kill old window
		frame.dispose();
		// Make new window
		initWindow(fullscreen);
		// Make new thread
		app.setFrameRate(Game.frameRate);
		app.startThread("Main Game");
	}

	// Make container window
	static void initWindow(boolean fullscreen) {
		// Make window
		frame = new JFrame();
		// Initial dimension
		if (fullscreen) {
			frame.setUndecorated(true);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setSize(screenSize.width, screenSize.height);
		} else {
			frame.setPreferredSize(new Dimension(defaultWid, defaultHei));
		}
		// Set fullscreen
		isFullscreen = fullscreen;
		// Add app
//		frame.add(app);
		frame.add(game);
		// Let user select (to get key inputs)
		frame.setFocusable(true);
		// Add inputs
		frame.addKeyListener(new KeyInput());
		MouseInput mouse = new MouseInput();
		frame.addMouseListener(mouse);
		frame.addMouseMotionListener(mouse);
		// Resizable
		frame.setResizable(!fullscreen);
		ScreenRender.addResizeListener(frame);
		// Window header
		frame.setTitle("Worms & Guns");
		// Close app on close
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Disable Game.running on close as well
//		frame.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent e) {
//				Game.running = false;
//				e.getWindow().dispose();
//			}
//		});
		// Pack dimensions
		frame.pack();
		// Center
		frame.setLocationRelativeTo(null);
		// Set icon
		frame.setIconImage(Game.textures.getImage("Icon.png"));
		// Display
		frame.setVisible(true);
	}

	// Main beginning
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				app = new WormsNGuns();
				initWindow(false);
			}
		});
	}

	public static boolean getIsFullscreen() {
		return isFullscreen;
	}

	// Loop each frame
	@Override
	protected void doLoop() {
		// Copy data
		Game.delta = delta;
		Game.frame = frameCount;
		// Do game logic
		game.logic();
		// Repaint
		game.repaint();
	}

}
