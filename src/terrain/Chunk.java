package terrain;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Game;
import util.OpenSimplexNoise;
import util.QuickMath;

public class Chunk {
	// Chunk pixel size
	public static final int areaDist = 512;
	public static final int chunkSize = 64;
	// Internal chunk constants
	protected static final int soilHeight = 10, soilMaxHeight = 30;

	// Array coordinates
	protected int x, y;
	// Noise function
	protected OpenSimplexNoise noise;
	// Display data
	protected BufferedImage chunkDisp;

	// Make chunk
	public Chunk(int x, int y, OpenSimplexNoise noi) {
		// Save vars
		this.x = x;
		this.y = y;
		noise = noi;
		// Make image
		updateImage();
	}

	// Noise offsets
	protected static final double ironOffset = 1000, goldOffset = 2000;

	// Make display image
	protected void updateImage() {
		// Nontransparent, size of chunk
		chunkDisp = new BufferedImage(chunkSize, chunkSize, BufferedImage.TYPE_INT_RGB);
		// Get graphics
		Graphics2D g = (Graphics2D) chunkDisp.getGraphics();
		// Underground chunk
		if (y >= 0) {
			// Background
			g.setColor(Game.textures.dirtColor);
			g.fillRect(0, 0, chunkSize, chunkSize);
			// Loop dark dirt
			g.setColor(Game.textures.darkDirtColor);
			for (int j = 0; j < chunkSize; j++) {
				int locY = (chunkSize * y + j);
				// Calc threshold for height
				double threshold = QuickMath.lerp(0.7, -0.7, locY / (10.0 * areaDist));
				for (int i = 0; i < chunkSize; i++) {
					int locX = (chunkSize * x + i);
					// Do eval
					if (noise.eval(locX / 100.0, locY / 100.0) > threshold) {
						g.drawLine(i, j, i, j);
					}
				}
			}
			// Loop gold
			g.setColor(Game.textures.goldColor);
			for (int j = 0; j < chunkSize; j++) {
				int locY = (chunkSize * y + j);
				// Calc threshold for height
				double threshold = QuickMath.lerp(0.825, 0.725, locY / (10.0 * areaDist));
				for (int i = 0; i < chunkSize; i++) {
					int locX = (chunkSize * x + i);
					// Do eval
					if (noise.eval(locX / 100.0, -locY / 120.0 + goldOffset) > threshold) {
						g.drawLine(i, j, i, j);
					}
				}
			}

			// Loop iron
			g.setColor(Game.textures.ironColor);
			for (int j = 0; j < chunkSize; j++) {
				int locY = (chunkSize * y + j);
				// Calc threshold for height
				double threshold = QuickMath.lerp(0.77, 0.67, locY / (10.0 * areaDist));
				for (int i = 0; i < chunkSize; i++) {
					int locX = (chunkSize * x + i);
					// Do eval
					if (noise.eval(locX / 260.0, -locY / 200.0 + ironOffset) > threshold) {
						g.drawLine(i, j, i, j);
					}
				}
			}

		}
		// Soil line
		if (y == 0) {
			for (int i = 0; i < chunkSize; i++) {
				// Calculate xpos used for noise
				double noiseX = (chunkSize * x + i) / 200.0;
				// Calculate y locs
				int yy = (int) Math.round((soilMaxHeight + soilMaxHeight * noise.eval(0, noiseX)) / 2);
				int topy = yy - soilHeight / 2, boty = yy + soilHeight / 2;
				// Draw sky line to top
				g.setColor(Game.textures.skyColor);
				g.drawLine(i, 0, i, topy);
				// Draw soil line
				g.setColor(Game.textures.soilColor);
				g.drawLine(i, topy, i, boty);
			}
		}
		// Sky chunk
		if (y < 0) {
			// Background
			g.setColor(Game.textures.skyColor);
			g.fillRect(0, 0, chunkSize, chunkSize);
			// Loop clouds
			g.setColor(Game.textures.cloudColor);
			for (int j = 0; j < chunkSize; j += 1) {
				int locY = (chunkSize * y + j);
				// Calc threshold for height
				double threshold = QuickMath.lerp(1.0, -0.4, -locY / (3.0 * areaDist));
				for (int i = 0; i < chunkSize; i += 1) {
					int locX = (chunkSize * x + i);
					// Do eval
					if (noise.eval(locX / 400.0, locY / 200.0) > threshold) {
						g.drawLine(i, j, i, j);
					}
				}

			}
		}

	}

	// Draw chunk
	public void draw(Graphics2D g) {
		// Draw image at position
		g.drawImage(chunkDisp, x * chunkSize, y * chunkSize, null);
	}
}
