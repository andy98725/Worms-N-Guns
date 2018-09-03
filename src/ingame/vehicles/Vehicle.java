package ingame.vehicles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import ingame.Board;
import main.Game;
import util.GraphicsFunctions;

public abstract class Vehicle {

	// Home
	protected Board parent;
	// Position stats
	protected double x, y, xvel, yvel, xacc, yacc;
	// Hitbox bounds
	protected Rectangle2D bounds;
	// Friction stats
	protected double fric = 1;
	protected boolean useFriction = true;
	// Max speed stats
	private double maxSpeed, maxSpeedSq;
	protected boolean useMaxSpeed = true;
	protected boolean sprinting = false;
	protected final double sprintMultiplier = 2.0;
	// Movement
	double moveSpeed, moveSpeedXMultiplier = 1, moveSpeedYMultiplier = 1;
	double gravity = 500;
	// Health stats
	protected int HP, mHP, armor;
	// Health display stats
	private double healthTimer = 0;
	private BufferedImage healthBar;
	// Grounded FSM
	protected final static int UNDERGROUND = 0, GROUNDED = 1, AIRBORNE = 2;
	protected int groundState = GROUNDED;
	// Hostile?
	protected boolean isHostile = false;

	// Create new vehicle
	public Vehicle(Board parent, int HP, int armor, double x, double y) {
		this.parent = parent;
		this.mHP = HP;
		this.HP = mHP;
		this.armor = armor;
		this.x = x;
		this.y = y;
		// Add to parent
		parent.addVehicle(this);
		// Make bounds
		bounds = new Rectangle2D.Double();
	}

	// Individual class defaults initialization
	protected abstract void initDefaults();

	// General logic
	public void logic() {
		physics();
		// Update bounds
		updateBounds();
	}

	// Do physics logic
	protected void physics() {
		// Do friction calculation
		if (useFriction) {
			double f = Math.min(1, Math.pow(fric, Game.delta));
			xvel *= f;
			yvel *= f;
		}
		// Do split order
		xvel += xacc * Game.delta / 2;
		yvel += yacc * Game.delta / 2;
		// Do max speed check
		double hsq = (xvel * xvel) + (yvel * yvel);
		if (useMaxSpeed && hsq > (sprinting ? maxSpeedSq * sprintMultiplier * sprintMultiplier : maxSpeedSq)) {
			double mult = (sprinting ? maxSpeed * sprintMultiplier : maxSpeed) / Math.sqrt(hsq);
			xvel *= mult;
			yvel *= mult;
		}
		x += xvel * Game.delta;
		y += yvel * Game.delta;
		xvel += xacc * Game.delta / 2;
		yvel += yacc * Game.delta / 2;
	}

	protected abstract void updateBounds();

	// Damage vehicle
	public void damage(int dam) {
		// Armor check
		if (dam <= armor)
			return;
		// Subtract HP
		HP -= (dam - armor);
		// Death check
		if (HP <= 0) {
			die();
		} else {
			// Show health bar for 2 seconds
			displayHealth(2);
		}
	}

	// Draw the vehicle
	public abstract void draw(Graphics2D g);

	public abstract void drawOutline(Graphics2D g, Color c);

	// Draw health potentially
	public void drawGUI(Graphics2D g) {
		// Display healthBar if time > 0
		if (healthTimer > 0) {
			// Translate to offset
			double xoff = x + healthX - healthWid / 2, yoff = y + healthY - healthHei / 2;
			g.translate(xoff, yoff);
			// Draw health
			g.drawImage(healthBar, 0, 0, null);
			// Translate back
			g.translate(-xoff, -yoff);

			// Decrement timer
			healthTimer -= Game.delta;
		}
	}

	// Set max speed
	protected void setMaxSpeed(double sp) {
		maxSpeed = sp;
		maxSpeedSq = maxSpeed * maxSpeed;
	}

	protected double getMaxSpeed() {
		return maxSpeed;
	}

	protected double getMaxSpeedSq() {
		return maxSpeedSq;
	}

	// Set sprinting
	public void setSprint(boolean s) {
		sprinting = s;
	}

	// Get x location
	public double getX() {
		return x;
	}

	// Get y location
	public double getY() {
		return y;
	}

	// Get point location
	public Point2D getLocation() {
		return new Point2D.Double(x, y);
	}

	// Get bounds
	public Rectangle2D getBounds() {
		return bounds;
	}

	// Get if vehicle is hostile to player
	public boolean getHostile() {
		return isHostile;
	}

	private static final int healthWid = 60, healthHei = 20, healthX = 0, healthY = -80;

	// Make health bar and set timer
	private void displayHealth(double time) {
		healthTimer = time;
		healthBar = GraphicsFunctions.makeHPBar(healthWid, healthHei, HP, mHP, false);
	}

	// Move the vehicle
	public abstract void accelerate(double x, double y);

	// Kill the vehicle
	protected void die() {
		// Remove from map
		parent.removeVehicle(this);
	}

	public boolean pointInsideBounds(Point2D p) {
		return (bounds.contains(p));
	}

	// See if point inside hitbox
	public abstract boolean pointInsideHitbox(Point2D p);

	public abstract boolean areaIntersectsHitbox(Area a);
}
