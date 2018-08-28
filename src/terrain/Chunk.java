package terrain;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Game;
import util.OpenSimplexNoise;
import util.QuickMath;

public class Chunk {
	// Chunk pixel size
	public static final int chunkSize = 512;
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

	// Make display image
	protected void updateImage() {
		// Nontransparent, size of chunk
		chunkDisp = new BufferedImage(chunkSize, chunkSize, BufferedImage.TYPE_INT_RGB);
		// Get graphics
		Graphics2D g = (Graphics2D) chunkDisp.getGraphics();
		// Underground chunk
		if (y >= 0) {
			// Background
			g.setColor(Game.textures.getRegularDirtColor());
			g.fillRect(0, 0, chunkSize, chunkSize);
			// Loop dark dirt
			g.setColor(Game.textures.getDarkDirtColor());
			for (int j = 0; j < chunkSize; j += 1) {
				int locY = (chunkSize * y + j);
				// Calc threshold for height
				double threshold = QuickMath.lerp(0.7, -0.7, locY / (10.0 * chunkSize));
				for (int i = 0; i < chunkSize; i += 1) {
					int locX = (chunkSize * x + i);
					// Do eval
					if (noise.eval(locX / 100.0, locY / 100.0) > threshold) {
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
				g.setColor(Game.textures.getSkyColor());
				g.drawLine(i, 0, i, topy);
				// Draw soil line
				g.setColor(Game.textures.getSoilColor());
				g.drawLine(i, topy, i, boty);
			}
		}
		// Sky chunk
		if (y < 0) {
			// Background
			g.setColor(Game.textures.getSkyColor());
			g.fillRect(0, 0, chunkSize, chunkSize);
			// Loop clouds
			g.setColor(Game.textures.getCloudColor());
			for (int j = 0; j < chunkSize; j += 1) {
				int locY = (chunkSize * y + j);
				// Calc threshold for height
				double threshold = QuickMath.lerp(1.0, -0.4, -locY / (3.0 * chunkSize));
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
