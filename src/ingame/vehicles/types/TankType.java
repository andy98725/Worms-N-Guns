package ingame.vehicles.types;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

public enum TankType implements VehicleType {
	// Basic vehicle
	BASIC,
	// Movement based vehicle
	SPEEDY,
	// Health/weapons based vehicle
	TANKY;

	@Override
	public RectangularShape[] getShape() {
		// Make body of tank
		RectangularShape[] ret = new RectangularShape[2];
		ret[0] = new Rectangle2D.Double(0, 0, getWid(), getHei());
		ret[1] = new Rectangle2D.Double(0, 0, getWid() / 2, getHei() / 4);
		return ret;
	}

	@Override
	public double getMaxSpeed() {
		switch (this) {
		default:
		case BASIC:
			return 200;
		case SPEEDY:
			return 250;
		case TANKY:
			return 150;
		}
	}

	@Override
	public double getAcceleration() {
		switch (this) {
		default:
		case BASIC:
			return 800;
		case SPEEDY:
			return 1200;
		case TANKY:
			return 600;
		}
	}

	@Override
	public double getFriction() {
		switch (this) {
		default:
		case BASIC:
			return 0.05;
		case SPEEDY:
			return 0.1;
		case TANKY:
			return 0.03;
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
			return new Color(0, 127, 0);
		case SPEEDY:
			return new Color(200, 150, 25);
		case TANKY:
			return new Color(100, 127, 100);
		}
	}

	@Override
	public Color getDebugOutline() {
		switch (this) {
		default:
		case BASIC:
			return new Color(0, 50, 0);
		case SPEEDY:
			return new Color(100, 50, 0);
		case TANKY:
			return new Color(50, 50, 50);
		}
	}

	@Override
	public int getHP() {
		switch (this) {
		default:
		case BASIC:
			return 200;
		case SPEEDY:
			return 160;
		case TANKY:
			return 300;
		}
	}

	@Override
	public int getArmor() {
		switch (this) {
		default:
		case BASIC:
			return 30;
		case SPEEDY:
			return 20;
		case TANKY:
			return 45;
		}
	}
	// Tank specific

	// Get width
	public double getWid() {
		switch (this) {
		default:
		case BASIC:
			return 60;
		case SPEEDY:
			return 40;
		case TANKY:
			return 80;
		}
	}

	// Get height
	public double getHei() {
		switch (this) {
		default:
		case BASIC:
			return 30;
		case SPEEDY:
			return 20;
		case TANKY:
			return 50;
		}
	}

	// Get jump speed
	public double getJumpVelocity() {
		switch (this) {
		default:
		case BASIC:
			return 300;
		case SPEEDY:
			return 380;
		case TANKY:
			return 280;
		}
	}

	// Get air jump count
	public int getAirJumps() {
		switch (this) {
		default:
		case BASIC:
			return 1;
		case SPEEDY:
			return 2;
		case TANKY:
			return 0;
		}
	}

	// Get air jump y speed
	public double getAirJumpYVel() {
		switch (this) {
		default:
		case BASIC:
			return 220;
		case SPEEDY:
			return 250;
		case TANKY:
			return 0;
		}
	}

	// Get air jump x speed
	public double getAirJumpXVel() {
		switch (this) {
		default:
		case BASIC:
			return 160;
		case SPEEDY:
			return 200;
		case TANKY:
			return 0;
		}
	}

}
