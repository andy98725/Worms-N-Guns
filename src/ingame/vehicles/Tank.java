package ingame.vehicles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import ingame.Board;

public class Tank extends Vehicle {
	protected double jumpVel = 400;
	// Rectangle hitbox dimensions
	protected double wid, hei;
	// Direction fsm
	protected static final int DIR_RIGHT = 0, DIR_LEFT = 1;
	protected int direction;

	public Tank(Board parent, int HP, int armor, double x, double y, double w, double h) {
		super(parent, HP, armor, x, y);
		// Set top speed
		setMaxSpeed(200);
		moveSpeed = 600;
		fric = 0.1;
		// Set rectangle dimensions
		wid = w;
		hei = h;
		// Set starting direction
		direction = DIR_RIGHT;
	}

	@Override
	public void accelerate(double x, double y) {
		// Cannot move in the air
		if (groundState == AIRBORNE) {
			return;
		}
		// X movement
		// Silence to 1
		if (Math.abs(x) > 1) {
			x = Math.signum(x);
		}
		// Dir
		if (x != 0) {
			if (x < 0)
				direction = DIR_LEFT;
			else
				direction = DIR_RIGHT;
		}
		// xacc
		xacc = x * moveSpeed;
		// Y movement
		if (y != 0) {
			if (y < 0) {
				// Jump!
				yvel = -jumpVel;
				// Air physics
				groundState = AIRBORNE;
				xacc = 0;
				yacc = gravity;
				useFriction = false;
				useMaxSpeed = false;
			}
		}

	}

	@Override
	public void draw(Graphics2D g) {
		// Define shell shape
		int sx = (int) Math.round(x - wid / 2);
		int sy = (int) Math.round(y - hei);
		int sw = (int) Math.round(wid);
		int sh = (int) Math.round(hei);
		// Define cannon shape
		int cw = (int) Math.round(wid / 3);
		int ch = (int) Math.round(hei / 6);
		int cy = (int) Math.round(y - hei / 2) - ch;
		int cx = (direction == DIR_RIGHT ? (int) Math.round(x + wid / 2) : (int) Math.round(x + -wid / 2) - cw);
		// Draw outer green
		// Deeper stroke
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(4.0f));
		g.setColor(new Color(0, 63, 0));
		g.drawRect(cx, cy, cw, ch);
		g.drawRect(sx, sy, sw, sh);
		// Now do inside
		g.setStroke(oldStroke);
		g.setColor(new Color(0, 127, 0));
		g.fillRect(cx, cy, cw, ch);
		g.fillRect(sx, sy, sw, sh);
	}

	// Physics
	@Override
	protected void physics() {
		super.physics();
		// Manage grounded FSM
		int floor = parent.getTerrain().getFloorHeight(x);
		if (y >= floor) {
			// Ground physics
			groundState = GROUNDED;
			y = floor;
			yvel = 0;
			yacc = 0;
			useFriction = true;
			useMaxSpeed = true;

		} else {
			groundState = AIRBORNE;
			// Air physics
			xacc = 0;
			yacc = gravity;
			useFriction = false;
			useMaxSpeed = false;
		}
	}

	@Override
	protected void die() {
		// Remove from map
		parent.removeVehicle(this);
	}

}
