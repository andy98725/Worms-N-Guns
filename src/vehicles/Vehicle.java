package vehicles;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import ingame.Board;
import main.Game;

public abstract class Vehicle {

	// Home
	protected Board parent;
	// Position stats
	protected double x, y, xvel, yvel, xacc, yacc;
	// Friction stats
	protected double fric;
	protected boolean useFriction = true;
	// Max speed stats
	private double maxSpeed, maxSpeedSq;
	protected boolean useMaxSpeed = true;
	protected boolean sprinting = false;
	protected final double sprintMultiplier = 1.5;
	// Health stats
	protected int HP, mHP, armor;

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
	}

	// General logic
	public void logic() {
		physics();
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

	// Do physics logic
	protected void physics() {
		// Do friction calculation
		if (useFriction) {
			double f = Math.min(1, Math.pow(fric, Game.delta));
			xvel *= f;
			yvel *= f;
		}
		// Do max speed check
		double hsq = (xvel * xvel) + (yvel * yvel);
		if (useMaxSpeed && hsq > (sprinting ? maxSpeedSq * sprintMultiplier * sprintMultiplier : maxSpeedSq)) {
			double mult = (sprinting ? maxSpeed * sprintMultiplier : maxSpeed) / Math.sqrt(hsq);
			xvel *= mult;
			yvel *= mult;
		}
		// Do split order
		xvel += xacc * Game.delta / 2;
		yvel += yacc * Game.delta / 2;
		x += xvel * Game.delta;
		y += yvel * Game.delta;
		xvel += xacc * Game.delta / 2;
		yvel += yacc * Game.delta / 2;
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
		}
	}

	// Move the vehicle
	public abstract void accelerate(double x, double y);

	// Draw the vehicle
	public abstract void draw(Graphics2D g);

	// Kill the vehicle
	protected abstract void die();
}
