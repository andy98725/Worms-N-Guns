package ingame.vehicles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;

import ingame.Board;
import ingame.vehicles.types.VehicleType;
import main.Game;
import util.GraphicsFunctions;

public abstract class Vehicle {

	// Home
	protected Board parent;
	// Vehicle type
	protected VehicleType type;
	// Position stats
	protected double x, y, xvel, yvel, xacc, yacc;
	protected RectangularShape[] segmentShape;
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
	double[] gravity = { 0, 250, 500 };
	protected boolean useGravity = true;
	// Health stats
	protected int HP, mHP, armor;
	// Health display stats
	private double healthTimer = 0;
	private BufferedImage healthBar;
	// Draw stats
	protected Color basicFill, basicOutline;
	// Grounded FSM
	protected final static int UNDERGROUND = 0, GROUNDED = 1, AIRBORNE = 2;
	protected int groundState = GROUNDED;
	// Hostile?
	protected boolean isHostile = false;

	// Create new vehicle
	public Vehicle(Board parent, double x, double y, VehicleType t) {
		this.parent = parent;
		this.type = t;
		this.x = x;
		this.y = y;
		// Add to parent
		parent.addVehicle(this);
		// Make bounds
		bounds = new Rectangle2D.Double();
		// Pull data
		pullFromEnum();
		// Update segents
		updateSegments();
		// Update FSM
		updateFSM();
		// Update bounds
		updateBounds();
	}

	// Pull data from enum
	protected void pullFromEnum() {
		// Get shape
		segmentShape = type.getShape();
		// Get speed stats
		setMaxSpeed(type.getMaxSpeed());
		moveSpeed = type.getAcceleration();
		fric = type.getFriction();
		gravity = type.getGravity();
		// Get draw data
		basicFill = type.getDebugFill();
		basicOutline = type.getDebugOutline();
		// Get health stats
		mHP = type.getHP();
		armor = type.getArmor();
		HP = mHP;
	}

	// General logic
	public void logic() {
		// Do physics
		physics();
		// Update segents
		updateSegments();
		// Update FSM
		updateFSM();
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
		// Gravity as well
		if (useGravity) {
			yvel += gravity[groundState] * Game.delta / 2;
		}
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
		if (useGravity) {
			yvel += gravity[groundState] * Game.delta / 2;
		}
	}

	protected abstract void updateSegments();

	// Update FSM
	protected void updateFSM() {
		// Check head at center
		int groundLevel = parent.getTerrain().getFloorHeight(segmentShape[0].getCenterX());
		// Is head grounded?
		if (segmentShape[0].getMaxY() >= groundLevel) {
			setFSMState(UNDERGROUND);
			return;
		}
		// Head is above ground. Check body.
		// Check if any segments are grounded
		for (int i = 1; i < segmentShape.length; i++) {
			// Get relevant segment
			RectangularShape seg = segmentShape[i];
			// Check grounded
			if (seg.getMaxY() >= parent.getTerrain().getFloorHeight(seg.getCenterX())) {
				setFSMState(GROUNDED);
				return;
			}
		}
		// None of body was grounded. Set to airborne.
		setFSMState(AIRBORNE);
		return;
	}

	protected abstract void setFSMState(int state);

	protected void updateBounds() {
		// Define bounds check
		double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
		for (int i = 0; i < segmentShape.length; i++) {
			// check each segment with rad
			RectangularShape seg = segmentShape[i];
			minX = Math.min(minX, seg.getMinX());
			maxX = Math.max(maxX, seg.getMaxX());
			minY = Math.min(minY, seg.getMinY());
			maxY = Math.max(maxY, seg.getMaxY());
		}
		// Set bounds
		bounds.setFrame(minX, minY, maxX - minX, maxY - minY);
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
		} else {
			// Show health bar for 2 seconds
			displayHealth(2);
		}
	}

	// Basic draw
	public void draw(Graphics2D g) {
		// Draw inside
		g.setColor(basicFill);
		for (int i = 0; i < segmentShape.length; i++) {
			g.fill(segmentShape[i]);
		}
	}

	// Outline strke
	private static final Stroke basic = new BasicStroke(6.0f);
	// Basic outline

	public void drawOutline(Graphics2D g, Color c) {
		// Draw border
		g.setColor(basicOutline);
		// Deeper stroke
		Stroke oldStroke = g.getStroke();
		g.setStroke(basic);
		for (int i = 0; i < segmentShape.length; i++) {
			g.draw(segmentShape[i]);
		}
		// restore
		g.setStroke(oldStroke);

	}

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

	// Set x location. Assume surface level
	public void setPosition(double x) {
		this.x = x;
		this.y = parent.getTerrain().getFloorHeight(x);
		// Update segents
		updateSegments();
		// Update FSM
		updateFSM();
		// Update bounds
		updateBounds();
	}

	// Set x/y location
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
		// Update segents
		updateSegments();
		// Update FSM
		updateFSM();
		// Update bounds
		updateBounds();
	}

	// Set x/y location and speed
	public void setPosition(double x, double y, double xv, double yv) {
		this.x = x;
		this.y = y;
		this.xvel = xv;
		this.yvel = yv;
		// Update segents
		updateSegments();
		// Update FSM
		updateFSM();
		// Update bounds
		updateBounds();

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

	// Get segment angles
	protected double[] getSegmentAngles() {
		// Declare
		double[] ret = new double[segmentShape.length];
		int endCase = ret.length - 1;
		// Base case
		ret[0] = Math.atan2(segmentShape[0].getCenterY() - segmentShape[1].getCenterY(),
				segmentShape[0].getCenterX() - segmentShape[1].getCenterX());
		// Middle cases
		for (int i = 1; i < endCase; i++) {
			ret[i] = Math.atan2(segmentShape[i - 1].getCenterY() - segmentShape[i + 1].getCenterY(),
					segmentShape[i - 1].getCenterX() - segmentShape[i + 1].getCenterX());
		}
		// End case
		ret[endCase] = Math.atan2(segmentShape[endCase - 1].getCenterY() - segmentShape[endCase].getCenterY(),
				segmentShape[endCase - 1].getCenterX() - segmentShape[endCase].getCenterX());
		// And return
		return ret;
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
	public boolean intersectsPoint(Point2D p) {
		// Check each segment
		for (int i = 0; i < segmentShape.length; i++) {
			// If inside segment, return true
			if (segmentShape[i].contains(p)) {
				return true;
			}
		}

		// Nope.
		return false;
	}

	// See if shape inside hitbox
	public boolean intersectsShape(Shape s) {
		// Make shape area
		Area a2 = new Area(s);
		// Check each segment
		for (int i = 0; i < segmentShape.length; i++) {
			// check if intersects bounds
			if (segmentShape[i].getBounds2D().intersects(s.getBounds2D())) {
				// Make areas and test
				Area a1 = new Area(segmentShape[i]);
				a1.intersect(a2);
				if (!a1.isEmpty()) {
					return true;
				}
			}
		}
		// None
		return false;

	}
}
