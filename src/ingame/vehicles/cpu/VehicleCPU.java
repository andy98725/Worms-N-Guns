package ingame.vehicles.cpu;

import ingame.vehicles.Vehicle;

public abstract class VehicleCPU {
	Vehicle target;
	protected VehicleCPU(Vehicle t) {
		target = t;
	}
	
	public abstract void logic();
}
