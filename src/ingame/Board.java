package ingame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import ingame.terrain.DrawContext;
import ingame.terrain.Terrain;
import ingame.vehicles.Tank;
import ingame.vehicles.Vehicle;
import ingame.vehicles.Worm;
import ingame.vehicles.types.TankType;
import ingame.vehicles.types.WormType;
import main.GlobalSettings;
import particles.Particle;

public class Board {

	// Background terrain
	protected Terrain terrain;
	// Player controlled vehicle
	protected Vehicle playerVehicle;
	// Contains all active vehicles
	protected ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
	// Contains particles to draw
	protected ArrayList<Particle> particles = new ArrayList<Particle>();
	// Display variables
	protected Camera camera;
	DrawContext context;
	protected int screenWid, screenHei;

	// Temporary/Quick startup
	public Board() {
		// Make terrain
		terrain = new Terrain(this);
		// Make quick worm
		quickWorm();
		// Make quick tank
//		quickTank();
		// Make camera for vehicle
		camera = new VehicleCamera(playerVehicle);
	}

	protected void quickWorm() {
		WormType type = WormType.SPEEDY;
		playerVehicle = new Worm(this, 0, 400, type);
		playerVehicle.setPosition(200, 400);
		context = DrawContext.WORM;
		// Set tap controls
		xtap = false;
		ytap = false;
	}

	protected void quickTank() {
		TankType type = TankType.BASIC;
		playerVehicle = new Tank(this, 0, 0, type);
		context = DrawContext.TANK;
		// Set tap controls
		xtap = false;
		ytap = true;
	}
	public Vehicle getPlayer() {
		return playerVehicle;
	}

	// Calculations
	public void logic() {
		// Do movement logic
		movementLogic();
		// Do particle logic
		particleLogic();
		// Do logic for each vehicle
		for (int i = 0; i < vehicles.size(); i++) {
			vehicles.get(i).logic();
		}
	}

	// Particle logic
	protected void particleLogic() {
		// Loop through particles
		for (int i = 0; i < particles.size(); i++) {
			// Get particle
			Particle p = particles.get(i);
			// Remove particle?
			if (p.logic()) {
				particles.remove(i);
				i--;
			}
		}
	}

	// Movement logic
	protected void movementLogic() {
		// Only if there's a player vehicle
		if (playerVehicle == null)
			return;
		// Keyboard controls?
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

	// Drawing
	public void draw(Graphics2D g) {
		// Use anti aliasing
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Translate to camera and clip
		camera.applyTranslation(g);
		g.setClip(camera.getBounds());
		// Draw background
		drawBackground(g);
		// Draw particles
		drawParticles(g);
		// Draw vehicles
		drawVehicles(g);
	}

	// Draw background stuff
	protected void drawBackground(Graphics2D g) {
		terrain.draw(g, camera, context);
	}

	// Draw particle stuff
	protected void drawParticles(Graphics2D g) {
		// Loop through particles
		for (int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);
			// If in bounds
			if (p.inRectangle(g.getClipBounds())) {
				// Draw
				p.draw(g);
			}
		}
	}

	// Draw vehicle stuff
	protected void drawVehicles(Graphics2D g) {
		// Determine which vehicles to draw
		ArrayList<Vehicle> draw = new ArrayList<Vehicle>();
		Rectangle2D bounds = camera.getBounds();
		for (int i = 0; i < vehicles.size(); i++) {
			Vehicle v = vehicles.get(i);
			// Always draw player vehicle so skip
			if (v == playerVehicle)
				continue;
			// Add to array iff clips intersect
			if (v.getBounds().intersects(bounds)) {
				draw.add(v);
			}
		}
		// Draw vehicle outline
		for (Vehicle v : draw) {
			// If hostile, red outline. Otherwise, black outline.
			v.drawOutline(g, (v.getHostile() ? Color.RED : Color.BLACK));
			v.draw(g);
		}
		// Draw vehicle GUIs
		for (Vehicle v : draw) {
			v.drawGUI(g);
		}
		// Draw player last
		playerVehicle.drawOutline(g, Color.DARK_GRAY);
		playerVehicle.draw(g);
		playerVehicle.drawGUI(g);
	}

	// Set screen size
	public void setDimensions(int wid, int hei) {
		screenWid = wid;
		screenHei = hei;
		// Set camera dimensions
		if (camera != null) {
			camera.setDimensions(wid, hei);
		}
		// Set terrain dimensions
		if (terrain != null) {
			terrain.setDimensions(wid, hei);
		}
	}

	// Get terrain
	public Terrain getTerrain() {
		return terrain;
	}

	// Add vehicle to array
	public void addVehicle(Vehicle v) {
		vehicles.add(v);
	}

	// Remove vehicle from array
	public void removeVehicle(Vehicle v) {
		vehicles.remove(v);
	}

	// Add particle to array
	public void addParticle(Particle p) {
		particles.add(p);
	}

	public boolean activeDamageCheck(Vehicle[] friendlies, Point2D check, int damage) {
		// Check each vehicle if point is inside
		for (int i = 0; i < vehicles.size(); i++) {
			Vehicle v = vehicles.get(i);
			// Make sure not in passed in list
			boolean isContained = false;
			for (Vehicle v2 : friendlies) {
				if (v == v2) {
					isContained = true;
					break;
				}
			}
			// Continue if in list
			if (isContained) {
				continue;
			}
			// Check if in hitbox
			if (v.intersectsPoint(check)) {
				// It is!
				v.damage(damage);
				return true;
			}
		}
		// None
		return false;
	}

	// Inputs:
	// Keyboard press. Returns if keypress is used.
	boolean rightDown, leftDown, upDown, downDown;
	boolean xtap, ytap;

	public boolean pressKey(int keycode) {
		// Keyboard controls
		if (GlobalSettings.getKeyboardControls()) {
			switch (keycode) {
			default:
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				upDown = true;
				// Tap reset
				if (ytap) {
					movementLogic();
					upDown = false;
				}
				return true;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				downDown = true;
				// Tap reset
				if (ytap) {
					movementLogic();
					downDown = false;
				}
				return true;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				leftDown = true;
				// Tap reset
				if (xtap) {
					movementLogic();
					leftDown = false;
				}
				return true;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				rightDown = true;
				// Tap reset
				if (xtap) {
					movementLogic();
					rightDown = false;
				}
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
		// Temp. damage
		Point2D mousePoint = new Point2D.Double(camera.getTLX() + mouseX, camera.getTLY() + mouseY);
		activeDamageCheck(new Vehicle[] {}, mousePoint, 10);
		return false;
	}

	public boolean mouseRelease() {
		return false;
	}
}
