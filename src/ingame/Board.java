package ingame;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import main.GlobalSettings;
import terrain.Chunk;
import terrain.Terrain;
import vehicles.Vehicle;
import vehicles.Worm;

public class Board {

	// Background terrain
	protected Terrain terrain;
	// Player controlled vehicle
	protected Vehicle playerVehicle;
	// Contains all active vehicles
	protected ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
	// Display variables
	protected Camera camera;
	protected int screenWid, screenHei;

	// Temporary/Quick startup
	public Board() {
		// Make quick terrain
		terrain = new Terrain();
		// Make quick worm
		playerVehicle = new Worm(this, 5, 0, 0, Chunk.chunkSize / 2, 10);
		// Make camera for vehicle
		camera = new Camera(playerVehicle);
	}

	// Calculations
	public void logic() {
		// Do movement logic
		movementLogic();
		// Do logic for each vehicle
		for (Vehicle v : vehicles) {
			v.logic();
		}
	}

	// Drawing
	public void draw(Graphics2D g) {
		// Use anti aliasing
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Translate to camera
		if (camera != null) {
			camera.applyTranslation(g);
		}
		// Draw background
		terrain.draw(camera, g);
		// Draw vehicles
		for (Vehicle v : vehicles) {
			v.draw(g);
		}
	}

	// Set screen size
	public void setDimensions(int wid, int hei) {
		screenWid = wid;
		screenHei = hei;
		if (camera != null) {
			camera.setDimensions(wid, hei);
		}
	}

	// Add vehicle to array
	public void addVehicle(Vehicle v) {
		vehicles.add(v);
	}

	// Remove vehicle from array
	public void removeVehicle(Vehicle v) {
		vehicles.remove(v);
	}

	// Movement logic
	protected void movementLogic() {
		// Only if there's a player vehicle
		if (playerVehicle == null)
			return;
		if (GlobalSettings.getKeyboardControls()) {
			// Use keyboard
			int xmov = 0, ymov = 0;
			if (leftDown)
				xmov--;
			if (rightDown)
				xmov++;
			if (upDown)
				ymov--;
			if (downDown)
				ymov++;
			// Move vehicle
			playerVehicle.accelerate(xmov, ymov);
			return;
		} else {
			// Use mouse
			double relX = mouseX - screenWid / 2;
			double relY = mouseY - screenHei / 2;
			double hypot = Math.hypot(relX, relY);
			// Check deadzone
			if (hypot <= mouseDeadzone) {
				// Do no accel
				playerVehicle.accelerate(0, 0);
				return;
			}
			// Multiply by speedzone
			relX /= mouseFullzone;
			relY /= mouseFullzone;
			// Move vehicle
			playerVehicle.accelerate(relX, relY);
			return;
		}
	}

	// Inputs:
	// Keyboard press. Returns if keypress is used.
	boolean rightDown, leftDown, upDown, downDown;

	public boolean pressKey(int keycode) {
		// Keyboard controls
		if (GlobalSettings.getKeyboardControls()) {
			switch (keycode) {
			default:
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				upDown = true;
				return true;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				downDown = true;
				return true;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				leftDown = true;
				return true;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				rightDown = true;
				return true;
			}
		}
		// Sprinting
		switch (keycode) {
		default:
			break;
		case KeyEvent.VK_SPACE:
			if (playerVehicle != null) {
				playerVehicle.setSprint(true);
				return true;
			}
			break;
		}
		return false;
	}

	// Keyboard release. Returns if key release is used.
	public boolean releaseKey(int keycode) {
		// Keyboard controls
		if (GlobalSettings.getKeyboardControls()) {
			switch (keycode) {
			default:
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				upDown = false;
				return true;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				downDown = false;
				return true;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				leftDown = false;
				return true;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				rightDown = false;
				return true;
			}
		}
		// Sprinting
		switch (keycode) {
		default:
			break;
		case KeyEvent.VK_SPACE:
			if (playerVehicle != null) {
				playerVehicle.setSprint(false);
				return true;
			}
			break;
		}

		return false;
	}

	// Mouse move
	protected int mouseDeadzone = 32;
	protected int mouseFullzone = 256;
	protected int mouseX, mouseY;

	public boolean mouseMove(int x, int y) {
		mouseX = x;
		mouseY = y;
		return false;
	}

	public boolean mousePress() {
		return false;
	}

	public boolean mouseRelease() {
		return false;
	}
}
