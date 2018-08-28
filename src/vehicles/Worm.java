package vehicles;

import java.awt.Color;
import java.awt.Graphics2D;

import ingame.Board;

public class Worm extends Vehicle {

	double speedSec = 300;
	double wormGrav = 700;

	public Worm(Board parent, int HP, int armor, int x, int y, int length) {
		super(parent, HP, armor, x, y);
		// Set friction (amount of momentum retained/frame approx)
		fric = 0.99;
		// Set max speed
		setMaxSpeed(300);
	}

	@Override
	protected void die() {
		// Remove from map
		parent.removeVehicle(this);
	}

	@Override
	public void draw(Graphics2D g) {
		// Temp test:
		g.setColor(Color.DARK_GRAY);
		g.fillOval((int) Math.round(x - 20), (int) Math.round(y - 20), 40, 40);

	}

	// Physics
	@Override
	protected void physics() {
		if (y < 0) {
			// Do gravity if airborne
			xacc = 0;
			yacc = wormGrav;
			useFriction = false;
			useMaxSpeed = false;
			super.physics();
			// Collided with earth?
			if(y >= 0) {
				// Slow down
				xvel /= 2;
				yvel /= 4;
				useFriction = true;
				useMaxSpeed = true;
			}
		} else {
			// Regular physics
			super.physics();
		}
	}

	// Accelerate vehicle
	@Override
	public void accelerate(double xx, double yy) {
		// Cannot move when airborne
		if (this.y < 0)
			return;
		// Calculate abs
		if (xx != 0 || yy != 0) {
			double mult = speedSec / Math.hypot(xx, yy);
			xx *= mult;
			yy *= mult;
		}
		// Set acceleration directly
		xacc = xx;
		yacc = yy;
	}

}
