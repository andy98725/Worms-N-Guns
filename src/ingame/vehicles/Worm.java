package ingame.vehicles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import ingame.Board;
import main.Game;

public class Worm extends Vehicle {

	// Movement stats
	double thetaNudge, thetaNudgeRange;
	// Segments stats
	int segments;
	double[] segmentYvel;
	double[] segmentLength;
	Ellipse2D.Double[] segmentShape;
	double segmentGravity;

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
		gravity = 500;
		segmentGravity = gravity;
		// Set nudge speed per second
		thetaNudge = 1;
		thetaNudgeRange = Math.PI / 2;
		// Make segments data
		segmentShape = new Ellipse2D.Double[segments];
		segmentYvel = new double[segments];
		segmentLength = new double[segments];
		// Calculate individual values
		double initialRad = 64;
		segmentShape[0] = new Ellipse2D.Double(x - initialRad / 2, y - initialRad / 2, initialRad, initialRad);
		segmentLength[0] = 32;
		segmentYvel[0] = 0;
		// Iterate
		for (int i = 1; i < segments; i++) {
			double rad = segmentShape[i - 1].getWidth() * 0.95;
			segmentLength[i] = 0.95 * segmentLength[i - 1];
			segmentShape[i] = new Ellipse2D.Double(x - rad / 2, y - rad / 2, rad, rad);
			segmentYvel[i] = 0;
		}
		// Update bounds
		updateBounds();
	}

	@Override
	public void draw(Graphics2D g) {
		// Draw inside
		g.setColor(Color.GRAY);
		for (int i = 0; i < segments; i++) {
			g.fill(segmentShape[i]);
		}
	}

	@Override
	public void drawOutline(Graphics2D g, Color c) {
		// Draw border
		g.setColor(c);
		// Deeper stroke
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(6.0f));
		for (int i = 0; i < segments; i++) {
			g.draw(segmentShape[i]);
		}
		// restore
		g.setStroke(oldStroke);

	}

	@Override
	protected void updateBounds() {
		// Define bounds check
		double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
		for (int i = 0; i < segments; i++) {
			// check each segment with rad
			Ellipse2D seg = segmentShape[i];
			minX = Math.min(minX, seg.getMinX());
			maxX = Math.max(maxX, seg.getMaxX());
			minY = Math.min(minY, seg.getMinY());
			maxY = Math.max(maxY, seg.getMaxY());
		}
		// Set bounds
		bounds.setFrame(minX, minY, maxX - minX, maxY - minY);
	}

	@Override
	public boolean pointInsideHitbox(Point2D p) {
		// Check each segment
		for (int i = 0; i < segments; i++) {
			// If inside segment, return true
			if (segmentShape[i].contains(p)) {
				return true;
			}
		}

		// Nope.
		return false;
	}

	@Override
	public boolean areaIntersectsHitbox(Area a) {
		// TODO Auto-generated method stub
		return false;
	}

	// Physics
	@Override
	protected void physics() {
		updateFSM();
		super.physics();
		// Move segments
		physicsSegments();

	}

	// Move segments
	protected void physicsSegments() {
		// Base case
		Ellipse2D base = segmentShape[0];
		base.setFrame(x - base.getWidth() / 2, y - base.getHeight(), base.getWidth(), base.getHeight());
		// Iteratively
		for (int i = 1; i < segments; i++) {
			// Get relevant segment
			Ellipse2D seg = segmentShape[i];
			// If aboveground, apply gravity
			if (seg.getMaxY() < parent.getTerrain().getFloorHeight(seg.getCenterX())) {
				// Do half delta approach to apply velocity
				segmentYvel[i] += Game.delta * segmentGravity / 2;
				seg.setFrame(seg.getX(), seg.getY() + Game.delta * segmentYvel[i], seg.getWidth(), seg.getHeight());
				segmentYvel[i] += Game.delta * segmentGravity / 2;
			} else {
				// Reset gravity
				segmentYvel[i] = 0;
			}
			// Only move if stretched
			Ellipse2D prevSeg = segmentShape[i - 1];
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

	// Update FSM
	protected void updateFSM() {
		// Check head
		int groundLevel = parent.getTerrain().getFloorHeight(segmentShape[0].getCenterX());
		// Is head grounded?
		if (segmentShape[0].getMaxY() >= groundLevel) {
			// If coming from air, slow down
			if (groundState == AIRBORNE) {
				xvel *= 0.8;
				yvel *= 0.4;
			}
			groundState = UNDERGROUND;
			// Regular physics
			useFriction = true;
			useMaxSpeed = true;
			moveSpeedXMultiplier = 1;
			moveSpeedYMultiplier = 1;
		} else {
			// Head is above ground
			groundState = AIRBORNE;
			// Check if any segments are on ground
			for (int i = 1; i < segments; i++) {
				// Get relevant segment
				Ellipse2D seg = segmentShape[i];
				// Check grounded
				if (seg.getMaxY() >= parent.getTerrain().getFloorHeight(seg.getCenterX())) {
					groundState = GROUNDED;
					break;
				}
			}
			// If airborne, check these
			if (groundState == AIRBORNE) {
				// Do gravity if airborne
				xacc = 0;
				yacc = gravity;
				useFriction = false;
				useMaxSpeed = false;
				moveSpeedXMultiplier = 0;
				moveSpeedYMultiplier = 0;
			} else {
				// Slowed down from underground
				useFriction = true;
				useMaxSpeed = true;
				moveSpeedXMultiplier = 0.5;
				moveSpeedYMultiplier = 0.8;
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
			double mult = moveSpeed / Math.max(1, Math.hypot(xx, yy));
			xx *= mult;
			yy *= mult;
		}
		// Set acceleration directly
		xacc = xx * moveSpeedXMultiplier;
		yacc = yy * moveSpeedYMultiplier;
		// If aboveground, use some gravity
		if (groundState == GROUNDED) {
			yacc += segmentGravity;
		}

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
