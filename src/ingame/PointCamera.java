package ingame;

public class PointCamera extends Camera {

	// Point location
	int x, y;

	// Static point, no vehicle
	public PointCamera(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	protected double getCenterX() {
		// Return x
		return x;
	}

	@Override
	protected double getCenterY() {
		// Return y
		return y;
	}
}
