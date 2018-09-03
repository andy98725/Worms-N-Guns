package ingame;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class Camera {
	// Display settings
	int screenWid, screenHei;

	// Get camera focus
	protected abstract double getCenterX();

	protected abstract double getCenterY();

	// Apply camera translation to graphics instance
	public void applyTranslation(Graphics2D g) {
		// Translate to location offset
		g.translate(-(getCenterX() - screenWid / 2), -(getCenterY() - screenHei / 2));
	}

	// Apply translation to point
	public void applyTranslation(Point2D p) {
		// Translate to location offset
		p.setLocation(p.getX() - (getCenterX() - screenWid / 2), p.getY() - (getCenterY() - screenHei / 2));
	}

	protected Rectangle2D getBounds() {
		// Get bounds from location
		return new Rectangle2D.Double(getCenterX() - screenWid / 2, getCenterY() - screenHei / 2, screenWid, screenHei);
	}

	// Get top left
	public int getTLX() {
		return (int) Math.round(getCenterX()) - screenWid / 2;
	}

	public int getTLY() {
		return (int) Math.round(getCenterY()) - screenHei / 2;
	}

	// Get bot right
	public int getBRX() {
		return (int) Math.round(getCenterX()) + screenWid / 2;
	}

	public int getBRY() {
		return (int) Math.round(getCenterY()) + screenHei / 2;
	}

	// Set screen size
	public void setDimensions(int wid, int hei) {
		screenWid = wid;
		screenHei = hei;
	}
}
