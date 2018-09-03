package ingame;

import ingame.vehicles.Vehicle;

public class VehicleCamera extends Camera {
	// Vehicle to track
	Vehicle tracking;

	// Tracking vehicle
	public VehicleCamera(Vehicle v) {
		this.tracking = v;
	}

	@Override
	protected double getCenterX() {
		// Return vehicle's x
		return tracking.getX();
	}

	@Override
	protected double getCenterY() {
		// Return vehicle's y
		return tracking.getY();
	}
}
