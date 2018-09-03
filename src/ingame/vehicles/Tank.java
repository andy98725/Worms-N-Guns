package ingame.vehicles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import ingame.Board;

public class Tank extends Vehicle {
	protected double jumpVel = 300;
	// Rectangle hitbox dimensions
	protected double wid, hei;
	// Direction fsm
	protected static final int DIR_RIGHT = 0, DIR_LEFT = 1;
	protected int direction;

	public Tank(Board parent, int HP, int armor, double x, double y, double w, double h) {
		super(parent, HP, armor, x, y);
		// Set rectangle dimensions
		wid = w;
		hei = h;
		// Init other settings
		initDefaults();
	}

	@Override
	protected void initDefaults() {
		// Set top speed
		setMaxSpeed(150);
		moveSpeed = 800;
		fric = 0.05;
		// Set starting direction
		direction = DIR_RIGHT;
		// Update bounds
		updateBounds();

	}

	@Override
	public void draw(Graphics2D g) {
		// Define shell shape
		int sx = (int) Math.round(-wid / 2);
		int sy = (int) Math.round(-hei);
		int sw = (int) Math.round(wid);
		int sh = (int) Math.round(hei);
		g.setColor(new Color(0, 127, 0));
		g.fill(bounds);
		// Shift to origin
		g.translate(x, y);
		g.fillRect(sx, sy, sw, sh);
		// Shift back
		g.translate(-x, -y);
	}

	@Override
	public void drawOutline(Graphics2D g, Color c) {
		// Deeper stroke
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(4.0f));
		// Define shell shape
		int sx = (int) Math.round(-wid / 2);
		int sy = (int) Math.round(-hei);
		int sw = (int) Math.round(wid);
		int sh = (int) Math.round(hei);
		g.setColor(new Color(0, 63, 0));
		g.draw(bounds);
		// Shift to origin
		g.translate(x, y);
		g.drawRect(sx, sy, sw, sh);
		// And reset
		g.setStroke(oldStroke);
		g.translate(-x, -y);
	}

	@Override
	protected void updateBounds() {
		bounds.setFrame(x - wid / 2, y - hei, wid, hei);
	}

	@Override
	public boolean areaIntersectsHitbox(Area a) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pointInsideHitbox(Point2D p) {
		// Check inside main hitbox
		if (x - wid / 2 <= p.getX() && x + wid / 2 >= p.getX() && y >= p.getY() && y - hei <= p.getY()) {
			// Yep!
			return true;
		}
		// Nope.
		return false;
	}

	// Physics
	@Override
	protected void physics() {
		super.physics();
		updateFSM();
	}

	protected void updateFSM() {
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
				yvel = -jumpVel * (sprinting ? Math.sqrt(sprintMultiplier) : 1.0);
				// Air physics
				groundState = AIRBORNE;
				xacc = 0;
				yacc = gravity;
				useFriction = false;
				useMaxSpeed = false;
			}
		}

	}

}
