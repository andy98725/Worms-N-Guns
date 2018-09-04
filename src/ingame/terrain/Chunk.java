package ingame.terrain;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Game;
import util.OpenSimplexNoise;
import util.QuickMath;

public class Chunk {
	// Chunk pixel size
	public static final int areaDist = 512;
	public static final int chunkSize = 256;
	// Internal chunk constants
	protected static final int soilHeight = 10, soilMaxHeight = 90;

	// Data types
	protected static final int CH_REGULAR = 0, CH_DARK = 1, CH_CLOUD = 2, CH_SKY = 3, CH_IRON = 4, CH_GOLD = 5,
			CH_OIL = 6;
	int[][] chunkData;
	boolean[][] chunkDataLock;
	// Floor height
	int[] floorHeight;

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
		genChunk();
	}

	// Noise offsets
	protected static final double ironRNGOffset = 1000, goldRNGOffset = 2000, oilRNGOffset = -1000;

	// Make display image
	protected void genChunk() {
		// Make arrays
		chunkData = new int[chunkSize][chunkSize];
		chunkDataLock = new boolean[chunkSize][chunkSize];
		// Nontransparent, size of chunk
		chunkDisp = new BufferedImage(chunkSize, chunkSize, BufferedImage.TYPE_INT_RGB);
		// Get graphics
		Graphics2D g = (Graphics2D) chunkDisp.getGraphics();
		// Underground chunk
		if (y >= 0) {
			// Background (Defaults to regular)
			g.setColor(Game.textures.dirtColor);
			g.fillRect(0, 0, chunkSize, chunkSize);

			// Loop CH_DARK
			g.setColor(Game.textures.darkDirtColor);
			for (int j = 0; j < chunkSize; j++) {
				int locY = (chunkSize * y + j);
				// Calc threshold for height
				double darkThreshold = QuickMath.lerp(0.7, -0.7, locY / (10.0 * areaDist));
				double darkLockThreshold = QuickMath.lerp(1.0, 0.5, locY / (10.0 * areaDist));
				double regularLockThreshold = QuickMath.lerp(-0.3, -0.9, locY / (10.0 * areaDist));
				for (int i = 0; i < chunkSize; i++) {
					int locX = (chunkSize * x + i);
					// Do eval
					double eval = noise.eval(locX / 100.0, locY / 100.0);
					if (eval > darkThreshold) {
						// Set point to dark
						chunkData[i][j] = CH_DARK;
						g.drawLine(i, j, i, j);
						// Check if lock
						if (eval > darkLockThreshold) {
							chunkDataLock[i][j] = true;
						}
						// check regular locked?
					} else if (eval < regularLockThreshold) {
						// Set point to regular and lock
						chunkData[i][j] = CH_REGULAR;
						chunkDataLock[i][j] = true;
					}
				}
			}
			// Loop CH_GOLD
			g.setColor(Game.textures.goldColor);
			for (int j = 0; j < chunkSize; j++) {
				// Get yloc
				int locY = (chunkSize * y + j);
				// Calc threshold for height
				double goldThreshold = QuickMath.lerp(0.82, 0.72, locY / (10.0 * areaDist));
				double goldLockThreshold = QuickMath.lerp(0.835, 0.735, locY / (10.0 * areaDist));
				for (int i = 0; i < chunkSize; i++) {
					// Skip if locked
					if (chunkDataLock[i][j])
						continue;
					// Get xloc
					int locX = (chunkSize * x + i);
					// Do eval
					double eval = noise.eval(locX / 100.0, -locY / 120.0 + goldRNGOffset);
					if (eval > goldThreshold) {
						chunkData[i][j] = CH_GOLD;
						g.drawLine(i, j, i, j);
						// Check if lock
						if (eval > goldLockThreshold) {
							chunkDataLock[i][j] = true;
						}
					}
				}
			}

			// Loop CH_IRON
			g.setColor(Game.textures.ironColor);
			// Height loop
			for (int j = 0; j < chunkSize; j++) {
				// Calculate locy
				int locY = (chunkSize * y + j);
				// Calc threshold for height
				double ironThreshold = QuickMath.lerp(0.78, 0.68, locY / (10.0 * areaDist));
				double ironLockThreshold = QuickMath.lerp(0.82, 0.72, locY / (10.0 * areaDist));
				// Width loop
				for (int i = 0; i < chunkSize; i++) {
					// Skip if locked
					if (chunkDataLock[i][j])
						continue;
					// Calculate locx
					int locX = (chunkSize * x + i);
					// Do eval
					double eval = noise.eval(locX / 260.0, -locY / 200.0 + ironRNGOffset);
					if (eval > ironThreshold) {
						g.drawLine(i, j, i, j);
						chunkData[i][j] = CH_IRON;
						// Check if lock
						if (eval > ironLockThreshold) {
							chunkDataLock[i][j] = true;
						}
					}
				}
			}

		}

		// Soil line
		if (y == 0) {
			// Calculate floor heights
			floorHeight = new int[chunkSize];
			for (int i = 0; i < chunkSize; i++) {
				// Calculate xpos used for noise
				double noiseX = (chunkSize * x + i) / 200.0;
				// Calculate y locs
				int yy = (int) Math.round((soilMaxHeight + soilMaxHeight * noise.eval(0, noiseX)) / 2);
				int topy = yy - soilHeight / 2, boty = yy + soilHeight / 2;
				// Get floor height
				floorHeight[i] = topy;
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
						// Make cloud
						g.drawLine(i, j, i, j);
						chunkData[i][j] = CH_CLOUD;
						chunkDataLock[i][j] = true;
					} else {
						// Default to sky
						chunkData[i][j] = CH_SKY;
						chunkDataLock[i][j] = true;
					}
				}

			}
		}

	}

	// Draw chunk
	public void draw(Graphics2D g, DrawContext context) {
		// Draw image at position
		g.drawImage(chunkDisp, x * chunkSize, y * chunkSize, null);
	}

	// Get floor height
	public int getFloorHeight(int x) {
		// Bad case
		if (y != 0)
			return 0;
		// Return array val
		return floorHeight[x];
	}
}
