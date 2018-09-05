package ingame.vehicles.types;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;

public enum WormType implements VehicleType {
	// Starting worm
	BASIC,
	// Small, moves quickly
	SPEEDY,
	// Large, slow, lots of health
	TANKY;

	// Get shape of worm
	@Override
	public RectangularShape[] getShape() {
		// Initial radius
		double radius = getInitialRadius();
		// Make segments
		RectangularShape[] ret = new Ellipse2D.Double[getSegmentCount()];
		// Iterate
		for (int i = 0; i < ret.length; i++) {
			ret[i] = new Ellipse2D.Double(-radius, -radius, 2 * radius, 2 * radius);
			radius *= 0.95;
		}
		// And return
		return ret;
	}

	@Override
	public double getMaxSpeed() {
		switch (this) {
		default:
		case BASIC:
			return 500;
		case SPEEDY:
			return 600;
		case TANKY:
			return 400;
		}
	}

	@Override
	public double getAcceleration() {
		switch (this) {
		default:
		case BASIC:
			return 1200;
		case SPEEDY:
			return 1600;
		case TANKY:
			return 800;
		}
	}

	@Override
	public double getFriction() {
		switch (this) {
		default:
		case BASIC:
			return 0.3;
		case SPEEDY:
			return 0.4;
		case TANKY:
			return 0.25;
		}
	}

	@Override
	public double[] getGravity() {
		// Gravity is the same for all
		return new double[] { 0, 250, 500 };
	}

	@Override
	public Color getDebugFill() {
		switch (this) {
		default:
		case BASIC:
			return new Color(127, 127, 127);
		case SPEEDY:
			return new Color(200, 200, 100);
		case TANKY:
			return new Color(100, 100, 200);
		}
	}

	@Override
	public Color getDebugOutline() {
		switch (this) {
		default:
		case BASIC:
			return new Color(63, 63, 63);
		case SPEEDY:
			return new Color(100, 100, 50);
		case TANKY:
			return new Color(50, 50, 100);
		}
	}

	@Override
	public int getHP() {
		switch (this) {
		default:
		case BASIC:
			return 100;
		case SPEEDY:
			return 60;
		case TANKY:
			return 200;
		}
	}

	@Override
	public int getArmor() {
		switch (this) {
		default:
		case BASIC:
			return 10;
		case SPEEDY:
			return 0;
		case TANKY:
			return 15;
		}
	}
	// Worm specific

	// Get nudge angle range
	public double getNudgeRange() {
		switch (this) {
		default:
		case BASIC:
			return Math.PI / 2;
		case SPEEDY:
			return Math.PI * 2 / 3;
		case TANKY:
			return Math.PI / 3;
		}
	}

	// Get nudge rad/s
	public double getNudgeAccel() {
		switch (this) {
		default:
		case BASIC:
			return 1;
		case SPEEDY:
			return 1.4;
		case TANKY:
			return 0.8;
		}
	}

	// Get segment count
	protected int getSegmentCount() {
		switch (this) {
		default:
		case BASIC:
			return 12;
		case SPEEDY:
			return 18;
		case TANKY:
			return 8;
		}
	}

	// Get initial size
	protected double getInitialRadius() {
		switch (this) {
		default:
		case BASIC:
			return 48;
		case SPEEDY:
			return 40;
		case TANKY:
			return 56;
		}

	}

	// Get distance between each segment
	public double[] getLengths() {
		// Make array
		double[] ret = new double[getSegmentCount()];
		// Initial length
		ret[0] = getInitialRadius() * 2 / 3;
		// Iterate
		for (int i = 1; i < ret.length; i++) {
			ret[i] = 0.95 * ret[i - 1];
		}
		// And return
		return ret;
	}

	// Get initial yvel double
	public double[] getInitialYvels() {
		// Default initialized to 0
		return new double[getSegmentCount()];
	}

}
