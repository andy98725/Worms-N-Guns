package ingame.terrain;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ingame.vehicles.Tank;
import ingame.vehicles.types.TankType;
import main.Game;
import util.OpenSimplexNoise;
import util.QuickMath;

public class Chunk {
	// Terrain
	protected final Terrain par;
	// Chunk pixel sizes
	public static final int areaDist = 512;
	public final int chunkSize;
	// Internal chunk constants
	protected static final int soilHeight = 10, soilMaxHeight = 90;

	// Data types
	protected static final int CH_REGULAR = 0, CH_DARK = 1, CH_CLOUD = 2, CH_SKY = 3, CH_IRON = 4, CH_GOLD = 5,
			CH_SOIL = 6;
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
	public Chunk(Terrain p, int x, int y, OpenSimplexNoise noi) {
		// Save vars
		this.par = p;
		this.x = x;
		this.y = y;
		noise = noi;
		chunkSize = Terrain.chunkSize;
		// Make image
		genChunk();
	}

	// Noise offsets
	protected static final double ironRNGOffset = 1000, goldRNGOffset = 2000, oilRNGOffset = -1000;

	// Generate chunk
	protected void genChunk() {
		// Make arrays
		chunkData = new int[chunkSize][chunkSize];
		chunkDataLock = new boolean[chunkSize][chunkSize];
		// Generate underground
		genUnderground();
		// Soil line
		genSurface();
		// Sky chunk
		genSky();
		// Make image
		createDisplayImage();
	}

	// Make chunk image
	protected void createDisplayImage() {
		// Nontransparent, size of chunk
		chunkDisp = new BufferedImage(chunkSize, chunkSize, BufferedImage.TYPE_INT_RGB);
		// Get graphics
		Graphics2D g = (Graphics2D) chunkDisp.getGraphics();
		// Loop through pixels
		for (int i = 0; i < chunkSize; i++) {
			for (int j = 0; j < chunkSize; j++) {
				// Color according to ID
				switch (chunkData[i][j]) {
				default:
				case CH_REGULAR:
					g.setColor(Game.textures.dirtColor);
					break;
				case CH_SOIL:
					g.setColor(Game.textures.soilColor);
					break;
				case CH_DARK:
					g.setColor(Game.textures.darkDirtColor);
					break;
				case CH_IRON:
					g.setColor(Game.textures.ironColor);
					break;
				case CH_GOLD:
					g.setColor(Game.textures.goldColor);
					break;
				case CH_SKY:
					g.setColor(Game.textures.skyColor);
					break;
				case CH_CLOUD:
					g.setColor(Game.textures.cloudColor);
					break;
				}
				// Color point
				g.drawLine(i, j, i, j);
			}
		}

	}

	protected void genUnderground() {
		// Don't do if above ground
		if (y < 0)
			return;
		// Loop CH_DARK and CH_REGULAR
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
					// Set
					chunkData[i][j] = CH_GOLD;
					// Check if lock
					if (eval > goldLockThreshold) {
						chunkDataLock[i][j] = true;
					}
				}
			}
		}

		// Loop CH_IRON
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
					// Set
					chunkData[i][j] = CH_IRON;
					// Check if lock
					if (eval > ironLockThreshold) {
						chunkDataLock[i][j] = true;
					}
				}
			}
		}
	}

	// Generate surface level
	protected void genSurface() {
		// Only done at surface level
		if (y != 0)
			return;
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
			// Sky loop
			for (int j = 0; j < topy; j++) {
				chunkData[i][j] = CH_SKY;
				chunkDataLock[i][j] = true;
			}
			// Soil loop
			for (int j = topy; j < boty; j++) {
				chunkData[i][j] = CH_SOIL;
				chunkDataLock[i][j] = true;
			}
		}
	}

	protected void genSky() {
		// Only do if sky
		if (y >= 0)
			return;
		// Loop clouds
		for (int j = 0; j < chunkSize; j += 1) {
			int locY = (chunkSize * y + j);
			// Calc threshold for height
			double threshold = QuickMath.lerp(1.0, -0.4, -locY / (3.0 * areaDist));
			for (int i = 0; i < chunkSize; i += 1) {
				int locX = (chunkSize * x + i);
				// Do eval
				if (noise.eval(locX / 400.0, locY / 200.0) > threshold) {
					// Make cloud
					chunkData[i][j] = CH_CLOUD;
					chunkDataLock[i][j] = true;
				} else {
					// Make sky
					chunkData[i][j] = CH_SKY;
					chunkDataLock[i][j] = true;
				}
			}

		}
	}

	// Generate vehicles
	public void genVehicles() {
		// Make tanks
		genTanks();
		// Make worms
		genWorms();
	}

	protected final static double tankConcentration = 1.8;

	// Make tanks
	protected void genTanks() {
		// Only generate at surface
		if (y != 0)
			return;
		if (y == 0) {
			// Do tanks
			double eval = noise.eval(x, x);
			int tankCount = (int) Math.round(tankConcentration / 4 + eval * tankConcentration / 2);
			for (int i = 0; i < tankCount; i++) {
				TankType type;
				// Determine type from digits
				double rand = (Math.abs(eval) * Math.pow(10, i + 2)) % 1.0;
				double rand2 = (Math.abs(eval) * Math.pow(8, i + 2)) % 1.0;
				if (rand < 0.6) {
					type = TankType.BASIC;
				} else if (rand < 0.9) {
					type = TankType.TANKY;
				} else {
					type = TankType.SPEEDY;
				}
				// Make new tank
				// Determine position in chunk
				int xx = x * chunkSize;
				int xoff = (int) Math.floor(rand2 * chunkSize);
				// Make tank at position
				new Tank(par.getBoard(), xx + xoff, floorHeight[xoff], type);
			}
		}
	}

	protected final static double wormConcentration = 1;

	protected void genWorms() {
		// Only generate at surface or underground
		if (y < 0)
			return;
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
