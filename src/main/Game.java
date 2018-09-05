package main;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import ingame.Board;

public class Game extends JPanel {
	private static final long serialVersionUID = 1L;
	// State
	public static boolean running;
	// Timing variables
	public static final int frameRate = 60;
	public static double delta;
	public static int frame;
	// Graphics variables
	public static Texturepack textures;
	public static double graphicsScaleFactor;
	// Ingame
	public static Board board;

	// Initialization
	public Game() {
		// Is running
		running = true;
		// Load textures
		textures = new Texturepack(getWorkingDirectory() + "sprites/");
		// Set freeform positioning
		setLayout(null);
		// Improves rendering
		setDoubleBuffered(true);
		// Initialize default globals
		GlobalSettings.initDefaults();

		// Temp make new board
		board = new Board();
	}
	// Set dimensions
	public void setDimensions(int wid, int hei) {
		setBounds(0, 0, wid, hei);
	}

	// Logic every frame
	public void logic() {
		// Board logic
		if (board != null) {
			board.logic();
		}
	}

	// Draw
	@Override
	public void paint(Graphics graphics) {
		// Draw board
		if (board != null) {
			board.draw((Graphics2D) graphics);
		}
	}

	// Calculate graphics quality
	public static void updateGraphicsQuality() {
		graphicsScaleFactor = GlobalSettings.getGraphicsQuality() * textures.getQuality();
	}

	// Get directory
	public static String getWorkingDirectory() {
		return System.getProperty("user.dir") + '/';
	}

}
