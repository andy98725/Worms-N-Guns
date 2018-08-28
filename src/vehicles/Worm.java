package vehicles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import ingame.Board;
import main.Game;

public class Worm extends Vehicle {

	// Physics stats
	double speedSec = 1000;
	double wormGrav = 500;
	double thetaNudge, thetaNudgeRange;
	// Grounded FSM
	protected final static int GROUNDED = 0, AIRBORNE = 1;
	protected int groundState = GROUNDED;
	// Segments stats
	int segments;
	double[] segmentX, segmentY, segmentYvel;
	double[] segmentLength, segmentRad;

	// Make new worm
	public Worm(Board parent, int HP, int armor, double x, double y, int length) {
		super(parent, HP, armor, x, y);
		// Set friction (amount of momentum retained/frame approx)
		fric = 0.3;
		// Set max speed
		setMaxSpeed(length * 40);
		// Set nudge speed per second
		thetaNudge = 1;
		thetaNudgeRange = Math.PI / 2;
		// Make segments data
		segments = length;
		segmentX = new double[segments];
		segmentY = new double[segments];
		segmentYvel = new double[segments];
		segmentRad = new double[segments];
		segmentLength = new double[segments];
		// Calculate individual values
		segmentX[0] = x;
		segmentY[0] = y;
		segmentYvel[0] = 0;
		segmentRad[0] = 36;
		segmentLength[0] = 32;
		// Iterate
		for (int i = 1; i < segments; i++) {
			segmentX[i] = x;
			segmentY[i] = y;
			segmentYvel[i] = 0;
			segmentRad[i] = 0.95 * segmentRad[i - 1];
			segmentLength[i] = 0.95 * segmentLength[i - 1];
		}
	}

	@Override
	protected void die() {
		// Remove from map
		parent.removeVehicle(this);
	}

	@Override
	public void draw(Graphics2D g) {
		// Draw border
		g.setColor(Color.DARK_GRAY);
		// Deeper stroke
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(6.0f));
		for (int i = 0; i < segments; i++) {
			int xx = (int) Math.round(segmentX[i] - segmentRad[i]);
			int yy = (int) Math.round(segmentY[i] - segmentRad[i]);
			int rr = (int) Math.round(2 * segmentRad[i]);
			g.drawOval(xx, yy, rr, rr);
		}
		// restore stroke
		g.setStroke(oldStroke);
		// Draw inside
		g.setColor(Color.GRAY);
		for (int i = 0; i < segments; i++) {
			int xx = (int) Math.round(segmentX[i] - segmentRad[i]);
			int yy = (int) Math.round(segmentY[i] - segmentRad[i]);
			int rr = (int) Math.round(2 * segmentRad[i]);
			g.fillOval(xx, yy, rr, rr);
		}
	}

	// Physics
	@Override
	protected void physics() {
		if (groundState == AIRBORNE) {
			// Do gravity if airborne
			xacc = 0;
			yacc = wormGrav;
			useFriction = false;
			useMaxSpeed = false;
			super.physics();
			// Collided with earth?
			if (y >= 0) {
				// Slow down
				xvel *= 0.8;
				yvel *= 0.4;
			}
		} else {
			// Regular physics
			useFriction = true;
			useMaxSpeed = true;
			super.physics();
		}
		// Move segments
		physicsSegments();
	}

	// Move segments
	protected void physicsSegments() {
		// Base case
		segmentX[0] = x;
		segmentY[0] = y;
		// Iteratively
		for (int i = 1; i < segments; i++) {
			// If aboveground, apply gravity
			if (segmentY[i] < 0) {
				segmentYvel[i] += Game.delta * wormGrav / 4;
				segmentY[i] += Game.delta * segmentYvel[i];
				segmentYvel[i] += Game.delta * wormGrav / 4;
			} else {
				// Reset gravity
				segmentYvel[i] = 0;
			}
			// Only move if stretched
			double x1 = segmentX[i] - segmentX[i - 1];
			double y1 = segmentY[i] - segmentY[i - 1];
			double dist = Math.hypot(x1, y1);
			if (dist <= segmentLength[i]) {
				continue;
			}
			// Reset gravity
			segmentYvel[i] /= Math.pow(2, Game.delta);
			// Confine to stretch
			double mult = segmentLength[i - 1] / dist;
			segmentX[i] = segmentX[i - 1] + x1 * mult;
			segmentY[i] = segmentY[i - 1] + y1 * mult;
		}
		// check FSM as well
		groundState = AIRBORNE;
		for (int i = 0; i < segments; i++) {
			if (segmentY[i] > 0) {
				groundState = GROUNDED;
				break;
			}
		}
	}

	// Accelerate vehicle
	@Override
	public void accelerate(double xx, double yy) {
		// Cannot move when airborne
		if (groundState == AIRBORNE) {
			return;
		}
		// Limit to speedSec
		if (xx != 0 || yy != 0) {
			double mult = speedSec / Math.max(1, Math.hypot(xx, yy));
			xx *= mult;
			yy *= mult;
		}
		// Set acceleration directly
		xacc = xx;
		yacc = yy;
		// Exit if 0,0 on either
		if ((xx == 0 && yy == 0) || (xvel == 0 && yvel == 0)) {
			return;
		}
		// Nudge current momentum in angle
		double theta = Math.atan2(yy, xx);
		double curTheta = Math.atan2(yvel, xvel);
		double diffTheta = theta - curTheta;
		// Handle gaps
		if (diffTheta >= 2 * Math.PI - thetaNudgeRange) {
			diffTheta -= 2 * Math.PI;
		}
		if (diffTheta <= -2 * Math.PI + thetaNudgeRange) {
			diffTheta += 2 * Math.PI;
		}
		// Check if in opposite hemisphere
		if (Math.abs(diffTheta) > thetaNudgeRange) {
			return;
		}
		// Multiply by nudge factor and delta
		diffTheta *= Math.min(1, thetaNudge * Game.delta);
		// Get new angle
		curTheta += diffTheta;
		// Nudge in direction
		double hypot = Math.hypot(xvel, yvel);
		xvel = hypot * Math.cos(curTheta);
		yvel = hypot * Math.sin(curTheta);
	}

}
