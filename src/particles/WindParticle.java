package particles;

import java.awt.Color;
import java.awt.Graphics2D;

import main.Game;

public class WindParticle extends Particle {

	// Distance of wind
	double dist;
	// Direction of wind
	protected int direction;
	public static final int DIR_LEFT = 0, DIR_RIGHT = 1, DIR_UP = 2, DIR_DOWN = 3;

	public WindParticle(int size, int dir, double x, double y, double d) {
		// Set timer
		super(size / 4.0f, x, y);
		// Set direction
		direction = dir;
		// Set speed
		dist = d * 4 / size;
	}

	// Move location
	@Override
	public boolean logic() {
		// Move loc
		double move = dist * Game.delta;
		switch (direction) {
		default:
		case DIR_RIGHT:
			x += move;
			break;
		case DIR_LEFT:
			x -= move;
			break;
		case DIR_DOWN:
			y += move;
			break;
		case DIR_UP:
			y -= move;
			break;
		}
		// Continue with regular
		return super.logic();
	}

	// Draw animation
	@Override
	public void draw(Graphics2D g) {
		// Calculate rotation
		double rot = 0;
		switch (direction) {
		default:
		case DIR_RIGHT:
			rot = 0;
			break;
		case DIR_LEFT:
			rot = Math.PI;
			break;
		case DIR_UP:
			rot = Math.PI / 2;
			break;
		case DIR_DOWN:
			rot = Math.PI * 3 / 2;
			break;
		}
		// Shift screen
		g.translate(x, y);
		g.rotate(rot);
		// TODO do animation
		g.setColor(Color.WHITE);
		int xrad = (int) Math.round(64 * time), yrad = (int) Math.round(48 * time);
		g.fillOval(-xrad, -yrad, 2 * xrad, 2 * yrad);

		// Undo shift
		g.rotate(-rot);
		g.translate(-x, -y);
	}

}
