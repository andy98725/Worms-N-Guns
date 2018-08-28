package ingame;

import java.awt.Graphics2D;

import ingame.vehicles.Vehicle;

public class Camera {
	// Vehicle to track
	Vehicle tracking;
	// Point location (if no vehicle)
	int x, y;
	// Display settings
	int screenWid, screenHei;

	// Static point, no vehicle
	public Camera(int x, int y) {
		this.x = x;
		this.y = y;
		this.tracking = null;
	}

	// Tracking vehicle
	public Camera(Vehicle v) {
		this.tracking = v;
	}

	// Apply camera translation to graphics instance
	public void applyTranslation(Graphics2D g) {
		if (tracking == null) {
			// Point translation
			g.translate(-(x - screenWid / 2), -(y - screenHei / 2));
		} else {
			// Translate to vehicle
			g.translate(-(tracking.getX() - screenWid / 2), -(tracking.getY() - screenHei / 2));
		}
	}

	// Get top left
	public int getTLX() {
		int xx = (tracking == null ? x : (int) Math.round(tracking.getX()));
		return xx - screenWid / 2;
	}

	public int getTLY() {
		int yy = (tracking == null ? x : (int) Math.round(tracking.getY()));
		return yy - screenHei / 2;
	}

	// Get bot right
	public int getBRX() {
		int xx = (tracking == null ? x : (int) Math.round(tracking.getX()));
		return xx + screenWid / 2;
	}

	public int getBRY() {
		int yy = (tracking == null ? x : (int) Math.round(tracking.getY()));
		return yy + screenHei / 2;
	}

	// Set screen size
	public void setDimensions(int wid, int hei) {
		screenWid = wid;
		screenHei = hei;
	}
}
