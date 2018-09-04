package particles;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import main.Game;

public abstract class Particle {

	// Particle location
	double x, y;
	// Particle timer
	float time;

	// Set timer for particle
	protected Particle(float t, double x, double y) {
		// Set time
		time = t;
		// Set location
		this.x = x;
		this.y = y;
	}

	// Maybe remove particle. Decrement time
	public boolean logic() {
		time -= Game.delta;
		return time <= 0;
	}

	// Draw particle
	public abstract void draw(Graphics2D g);


	// Margin of error
	protected double boundsError = 128;
	// Get if in bounds
	public boolean inRectangle(Rectangle r) {
		if (x + boundsError < r.getMinX()) {
			return false;
		}
		if (x - boundsError > r.getMaxX()) {
			return false;
		}
		if (y + boundsError < r.getMinY()) {
			return false;
		}
		if (y - boundsError > r.getMaxY()) {
			return false;
		}
		return true;
	}
}
