package ingame.vehicles;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;

import ingame.Board;
import main.Game;

public class Worm extends Vehicle {

	// Movement stats
	double thetaNudge, thetaNudgeRange;
	// Segments stats
	int segments;
	double[] segmentYvel;
	double[] segmentLength;

	// Make new worm
	public Worm(Board parent, int HP, int armor, double x, double y, int length) {
		super(parent, HP, armor, x, y);
		// Save segments
		segments = length;
		// Init other settings
		initDefaults();
	}

	@Override
	protected void initDefaults() {
		// Set friction (amount of momentum retained/frame approx)
		fric = 0.3;
		// Set physics data
		setMaxSpeed(segments * 40);
		moveSpeed = 1400;
		gravity = new double[] { 0, 250, 500 };
		// Set nudge speed per second
		thetaNudge = 1;
		thetaNudgeRange = Math.PI / 2;
		// Draw data
		basicFill = Color.GRAY;
		basicOutline = Color.DARK_GRAY;
		// Make segments data
		segmentShape = new Ellipse2D.Double[segments];
		segmentYvel = new double[segments];
		segmentLength = new double[segments];
		// Calculate individual values
		double initialRad = 32;
		segmentShape[0] = new Ellipse2D.Double(x - initialRad, y - initialRad, 2*initialRad, 2*initialRad);
		segmentLength[0] = 32;
		segmentYvel[0] = 0;
		// Iterate
		for (int i = 1; i < segments; i++) {
			double len = segmentShape[i - 1].getWidth() * 0.95;
			segmentLength[i] = 0.95 * segmentLength[i - 1];
			segmentShape[i] = new Ellipse2D.Double(x - len / 2, y - len / 2, len, len);
			segmentYvel[i] = 0;
		}
		// Update bounds
		updateBounds();
	}

	@Override
	protected void setFSMState(int state) {
		// Only do updates
		if (state == groundState)
			return;
		// Check state
		switch (state) {
		default:
		case UNDERGROUND: // Set to underground
			// Slow down velocities
			xvel *= 0.75;
			yvel *= 0.35;
			// Regular physics
			useGravity = false;
			useFriction = true;
			useMaxSpeed = true;
			moveSpeedXMultiplier = 1;
			moveSpeedYMultiplier = 1;
			groundState = UNDERGROUND;
			break;
		case GROUNDED: // Set to grounded
			useGravity = true;
			useFriction = true;
			useMaxSpeed = true;
			moveSpeedXMultiplier = 0.5;
			moveSpeedYMultiplier = 0.8;
			groundState = GROUNDED;
			break;
		case AIRBORNE: // Set to airborne
			useGravity = true;
			useFriction = false;
			useMaxSpeed = false;
			// No motion in air
			xacc = 0;
			yacc = 0;
			moveSpeedXMultiplier = 0;
			moveSpeedYMultiplier = 0;
			groundState = AIRBORNE;
			break;
		}
	}

	// Move segments
	@Override
	protected void updateSegments() {
		// Base case
		RectangularShape base = segmentShape[0];
		base.setFrame(x - base.getWidth() / 2, y - base.getHeight(), base.getWidth(), base.getHeight());
		// Iteratively
		for (int i = 1; i < segments; i++) {
			// Get relevant segment
			RectangularShape seg = segmentShape[i];
			// If aboveground, apply gravity
			if (seg.getMaxY() < parent.getTerrain().getFloorHeight(seg.getCenterX())) {
				// Do half delta approach to apply velocity
				segmentYvel[i] += Game.delta * gravity[groundState] / 2;
				seg.setFrame(seg.getX(), seg.getY() + Game.delta * segmentYvel[i], seg.getWidth(), seg.getHeight());
				segmentYvel[i] += Game.delta * gravity[groundState] / 2;
			} else {
				// Reset gravity
				segmentYvel[i] = 0;
			}
			// Only move if stretched
			RectangularShape prevSeg = segmentShape[i - 1];
			double x1 = seg.getCenterX() - prevSeg.getCenterX();
			double y1 = seg.getCenterY() - prevSeg.getCenterY();
			double dist = Math.hypot(x1, y1);
			if (dist <= segmentLength[i]) {
				continue;
			}
			// Reset gravity
			segmentYvel[i] /= Math.pow(2, Game.delta);
			// Stretch to bound
			double mult = segmentLength[i - 1] / dist;
			double newX = prevSeg.getCenterX() + x1 * mult;
			double newY = prevSeg.getCenterY() + y1 * mult;
			double w = seg.getWidth();
			double h = seg.getHeight();
			seg.setFrame(newX - w / 2, newY - h / 2, w, h);
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
			double mult = moveSpeed / Math.max(1, Math.hypot(xx, yy));
			xx *= mult;
			yy *= mult;
		}
		// Set acceleration directly
		xacc = xx * moveSpeedXMultiplier;
		yacc = yy * moveSpeedYMultiplier;

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
