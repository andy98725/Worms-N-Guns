package main;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import util.Debug;

public class WormsNGuns extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	// Container
	public static JFrame frame;
	// Application
	public static WormsNGuns app;
	// Game object
	public static Game game;

	// Main thread
	private Thread mainLoop;
	// Display settings
	protected static boolean isFullscreen = false;
	public static final int defaultWid = 1280, defaultHei = 1024;
	// Init defaults
	protected WormsNGuns() {
		// Make game
		game = new Game();
		// Add to rendering
		add(game);
	}
	public void setDimension(int wid, int hei) {
		setBounds(0, 0, wid, hei);
		game.setBounds(0, 0, wid, hei);
	}
	
	// Fullscreen toggle
	public static void refreshWindow(boolean fullscreen) {
		// Thread
		app.mainLoop.interrupt();
		// Kill old window
		frame.dispose();
		// Make new window
		initWindow(fullscreen);
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
		frame.add(app);
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

	// On window run, start rendering
	@Override
	public void addNotify() {
		super.addNotify();
		mainLoop = new Thread(this);
		mainLoop.start();
	}

	// Main loop
	@Override
	public void run() {
		// Declare timing variables
		long prevFrame = System.nanoTime(), sleep = 0;
		Game.time = 0;
		while (Game.running && !Thread.currentThread().isInterrupted()) {
			// Nanoseconds to seconds
			Game.delta = (System.nanoTime() - prevFrame) / 1000000000.0;
			Game.time += Game.delta;
			// Update frame timing
			prevFrame = System.nanoTime();
			Game.frame++;
			// Do game logic
			game.logic();
			// Repaint
			repaint();
			// Calculate pause time (in MS)
			sleep = (long) (1000.0 / Game.frameRate) - (System.nanoTime() - prevFrame) / 1000000;
			if (sleep < 0)
				sleep = 0;
			// Delay frame
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				System.out.println("Interrupted: " + e.getMessage());
				Debug.log("Interrupted: " + e.getMessage());
				return;
			}

		}

	}

	public static boolean getIsFullscreen() {
		return isFullscreen;
	}

}
