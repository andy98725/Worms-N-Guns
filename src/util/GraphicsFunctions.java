package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public abstract class GraphicsFunctions {
	public static final double defaultIndicatorWidth = 64;

	public static Color blend(Color c0, Color c1, double lerp) {
		// Failsafes
		if (lerp >= 1)
			return c1;
		if (lerp <= 0)
			return c0;
		double totalAlpha = c0.getAlpha() + c1.getAlpha();
		double weight0 = (c0.getAlpha() / totalAlpha + 1 - lerp) / 2;
		double weight1 = (c1.getAlpha() / totalAlpha + lerp) / 2;

		double r = weight0 * c0.getRed() + weight1 * c1.getRed();
		double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
		double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
		double a = Math.max(c0.getAlpha(), c1.getAlpha());

		return new Color((int) r, (int) g, (int) b, (int) a);
	}

	final static Color hpbg = new Color(200, 200, 200), hpfull = new Color(0, 200, 0), hpempty = new Color(200, 0, 0);

	public static BufferedImage makeHPBar(int w, int h, double HP, double mHP, boolean showHP, Color full,
			Color empty) {
		// Find bevel by area
		final int bevel = w * h / 256;
		BufferedImage ret = new BufferedImage(w + 1, h + 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) ret.getGraphics();
		// Antialiasing
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Font stuff
		// Background bar
		g.setColor(hpbg);
		g.fillRoundRect(0, 0, w, h, bevel, bevel);
		g.setColor(Color.BLACK);
		g.drawRoundRect(0, 0, w, h, bevel, bevel);
		// Health bar
		g.setColor(blend(empty, full, HP / mHP));
		g.fillRoundRect(0, 0, (int) (w * HP / mHP), h, bevel, bevel);
		g.setColor(Color.BLACK);
		g.drawRoundRect(0, 0, (int) (w * HP / mHP), h, bevel, bevel);
		// Text
		if (showHP) {
			g.setFont(new Font("Dialog", Font.BOLD, h - 3));
			int stx = w / 2;
			int sty = h / 2;
			drawCenteredText(g, Integer.toString((int) HP) + '/' + Integer.toString((int) mHP), stx, sty);
		}

		g.dispose();
		return ret;
	}

	// Default
	public static BufferedImage makeHPBar(int w, int h, double HP, double mHP, boolean showHP) {
		return makeHPBar(w, h, HP, mHP, showHP, hpfull, hpempty);
	}

	// Draw text centered on x and y
	public static void drawCenteredText(Graphics2D g, String text, float x, float y) {
		FontMetrics metrics = g.getFontMetrics();
		float tlx = x - metrics.stringWidth(text) / 2;
		float tly = y - metrics.getHeight() / 2 + metrics.getAscent();
		g.drawString(text, tlx, tly);
	}

	public static final int outlineBorder = 2;

}
