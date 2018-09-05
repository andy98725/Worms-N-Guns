package ingame.terrain;

import java.awt.Graphics2D;
import java.util.Random;

import ingame.Board;
import ingame.Camera;
import ingame.vehicles.Vehicle;
import main.Game;
import util.BiHashMap;
import util.OpenSimplexNoise;
import util.ThreadLoop;

public class Terrain extends ThreadLoop {
	// Board
	protected Board par;
	// Noise function
	protected OpenSimplexNoise noise;
	// BiHashmaps of chunks
	protected BiHashMap<Integer, Integer, Chunk> activeChunks, chunkData;
	// Chunk management data
	public static int activeChunkLength;
	public static int minActiveChunkLength = 5;
	public static final int chunkSize = 512;

	// New Terrain
	public Terrain(Board s) {
		// Save board
		par = s;
		// Declare chunkmap
		activeChunks = new BiHashMap<Integer, Integer, Chunk>();
		// Noise
		// for testing, use no seed
//		noise = new OpenSimplexNoise();
		// Use random seed
		noise = new OpenSimplexNoise(new Random().nextLong());
		// Do generation
		// Approx. once every 4 frames
		setFrameRate(Game.frameRate / 4);
		startThread("Terrain Generation");
	}

	public void draw(Graphics2D g, Camera cam, DrawContext context) {
		// Get bounds
		int minx = pixelToChunk(cam.getTLX());
		int miny = pixelToChunk(cam.getTLY());
		int maxx = pixelToChunk(cam.getBRX());
		int maxy = pixelToChunk(cam.getBRY());
		// Loop through and draw chunks
		for (int i = minx; i <= maxx; i++) {
			for (int j = miny; j <= maxy; j++) {
				// Draw chunk
				getChunk(i, j).draw(g, context);
			}
		}
	}

	// Set dimensions
	public void setDimensions(int w, int h) {
		// Handle activeChunks count
		int chunkDispWid = w / chunkSize + 2;
		int chunkDispHei = h / chunkSize + 2;
		activeChunkLength = Math.max(minActiveChunkLength, Math.max(chunkDispWid, chunkDispHei));
	}

	protected int pixelToChunk(double val) {
		return (int) Math.floor(((double) val) / chunkSize);
	}

	protected Chunk getChunk(int x, int y) {
		Chunk ret = activeChunks.get(x, y);
		// Return if exists
		if (ret != null)
			return ret;
		// Doesn't exist. Generate, then return
		ret = new Chunk(this, x, y, noise);
		activeChunks.put(x, y, ret);
		// Generate vehicles after
		ret.genVehicles();
		return ret;
	}

	// Get board
	public Board getBoard() {
		return par;
	}

	// Get height of floor at x location
	public int getFloorHeight(int x) {
		int cx = pixelToChunk(x);
		return getChunk(cx, 0).getFloorHeight(x - cx * chunkSize);
	}

	// Get height of floor at x location
	public int getFloorHeight(double x) {
		return getFloorHeight((int) Math.round(x));
	}

	// Check if there's any terrain to spawn
	@Override
	protected void doLoop() {
		System.out.println(Game.frame);
		// Ensure player exists
		Vehicle player = par.getPlayer();
		if (player == null)
			return;
		// Get center location
		int x = pixelToChunk(player.getX());
		int y = pixelToChunk(player.getY());
		// Loop rectangle around player and ensure spawned
		for (int i = x - activeChunkLength / 2; i <= x + activeChunkLength / 2; i++) {
			for (int j = y - activeChunkLength / 2; j <= y + activeChunkLength / 2; j++) {
				getChunk(i, j);
			}
		}
	}
}
