package ingame.vehicles.types;

import java.awt.Color;
import java.awt.geom.RectangularShape;

public interface VehicleType {

	// Hitbox stats
	public RectangularShape[] getShape();

	// Speed stats
	public double getMaxSpeed();

	public double getAcceleration();

	public double getFriction();

	public double[] getGravity();

	// Draw stats
	public Color getDebugFill();

	public Color getDebugOutline();

	// Health stats
	public int getHP();

	public int getArmor();
}
