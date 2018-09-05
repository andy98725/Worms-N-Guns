package ingame.vehicles;

import java.util.Random;

import ingame.Board;
import ingame.vehicles.types.TankType;
import main.Game;
import particles.WindParticle;

public class Tank extends Vehicle {
	// Width and height
	protected double wid, hei;
	// Jump speed
	protected double jumpVelocity;
	// Direction fsm
	protected static final int DIR_RIGHT = 0, DIR_LEFT = 1;
	protected int direction;
	// Air jumps
	protected int airJumps, currentAirJumps;
	protected double airJumpTimer;
	protected double airJumpX, airJumpY;

	public Tank(Board parent, double x, double y, TankType t) {
		super(parent, x, y, t);
		// Set starting direction
		direction = new Random().nextBoolean() ? DIR_LEFT : DIR_RIGHT;
	}

	@Override
	protected void pullFromEnum() {
		// Do super
		super.pullFromEnum();
		// Get tankType
		TankType tankType = (TankType) type;
		// Set dimensions
		wid = tankType.getWid();
		hei = tankType.getHei();
		// Set jump stats
		jumpVelocity = tankType.getJumpVelocity();
		airJumps = tankType.getAirJumps();
		airJumpX = tankType.getAirJumpXVel();
		airJumpY = tankType.getAirJumpYVel();
	}

	// Decrement air jump timer
	@Override
	public void logic() {
		super.logic();
		// Decrement
		if (airJumpTimer > 0) {
			airJumpTimer -= Game.delta;
		}
	}

	// Position segments off x/y
	@Override
	protected void updateSegments() {
		// Update yposition if grounded (check velocity for safety)
		if (groundState == GROUNDED && yvel == 0) {
			y = parent.getTerrain().getFloorHeight(x);
		}
		// 0 is main tank
		double wid = segmentShape[0].getWidth();
		double hei = segmentShape[0].getHeight();
		segmentShape[0].setFrame(x - wid / 2, y - hei, wid, hei);
		// 1 is cannon
		double x1 = x - wid / 2, x2 = x + wid / 2;
		double y1 = y - hei * 2 / 3;
		wid = segmentShape[1].getWidth();
		hei = segmentShape[1].getHeight();
		segmentShape[1].setFrame(direction == DIR_LEFT ? x1 - wid : x2, y1 - hei / 2, wid, hei);
	}

	// Set physics from FSM
	@Override
	protected void setFSMState(int state) {
		switch (state) {
		default:
		case GROUNDED:
			// Set y each frame
			y = parent.getTerrain().getFloorHeight(x);
			// Refresh air jumps
			currentAirJumps = airJumps;
			// Ground physics
			yvel = 0;
			yacc = 0;
			useGravity = false;
			useFriction = true;
			useMaxSpeed = true;
			// Set
			groundState = GROUNDED;
			break;
		case AIRBORNE:
			// Air physics
			xacc = 0;
			yacc = 0;
			useGravity = true;
			useFriction = false;
			useMaxSpeed = false;
			groundState = AIRBORNE;
			break;
		}
	}

	// Move (x) or jump (y)
	@Override
	public void accelerate(double x, double y) {
		if (groundState == AIRBORNE) {
			// Air jump?
			if (currentAirJumps > 0 && y < 0 && airJumpTimer <= 0) {
				currentAirJumps--;
				airJumpTimer = 0.2;
				// Set velocities
				yvel = -airJumpY * (sprinting ? Math.sqrt(sprintMultiplier) : 1.0);
				// Add particles from jump and set direction/velocity
				switch ((int) Math.signum(x)) {
				default:
				case 0:
					xvel = 0;
					// Add particles
					// Down particle, left position
					parent.addParticle(
							new WindParticle(2, WindParticle.DIR_DOWN, this.x - wid / 2, this.y + hei / 4, 100));
					// Down particle, right position
					parent.addParticle(
							new WindParticle(2, WindParticle.DIR_DOWN, this.x + wid / 2, this.y + hei / 4, 100));
					break;
				case -1:
					xvel = -airJumpX * (sprinting ? Math.sqrt(sprintMultiplier) : 1.0);
					direction = DIR_LEFT;
					// Add particles
					// Down particle, left position
					parent.addParticle(
							new WindParticle(2, WindParticle.DIR_DOWN, this.x - wid / 2, this.y + hei / 4, 100));
					// Right particle, right position
					parent.addParticle(
							new WindParticle(2, WindParticle.DIR_RIGHT, this.x + wid / 2, this.y + hei / 4, 100));
					break;
				case 1:
					xvel = airJumpX * (sprinting ? Math.sqrt(sprintMultiplier) : 1.0);
					direction = DIR_RIGHT;
					// Add particles
					// Left particle, left position
					parent.addParticle(
							new WindParticle(2, WindParticle.DIR_LEFT, this.x - wid / 2, this.y + hei / 4, 100));
					// Down particle, right position
					parent.addParticle(
							new WindParticle(2, WindParticle.DIR_DOWN, this.x + wid / 2, this.y + hei / 4, 100));
					break;
				}
			}
			return;
		}
		// Ground movement:
		// X movement
		// ceil to 1
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
				yvel = -jumpVelocity * (sprinting ? Math.sqrt(sprintMultiplier) : 1.0);
				airJumpTimer = 0.2;
				setFSMState(AIRBORNE);
			}
		}

	}

}
