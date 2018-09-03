package ingame.terrain;

import java.awt.Graphics2D;
import java.util.Random;

import ingame.Camera;
import util.BiHashMap;
import util.OpenSimplexNoise;

public class Terrain {
	// Bi hashmap of chunks
	protected BiHashMap<Integer, Integer, Chunk> chunkmap;
	// Noise function
	protected OpenSimplexNoise noise;

	// New Terrain
	public Terrain() {
		// Declare chunkmap
		chunkmap = new BiHashMap<Integer, Integer, Chunk>();
		// Noise
//		// for testing, use no seed
//		noise = new OpenSimplexNoise();
		// Use random seed
		noise = new OpenSimplexNoise(new Random().nextLong());
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

	protected int pixelToChunk(double val) {
		return (int) Math.floor(((double) val) / Chunk.chunkSize);
	}

	protected Chunk getChunk(int x, int y) {
		Chunk ret = chunkmap.get(x, y);
		// Return if exists
		if (ret != null)
			return ret;
		// Doesn't exist. Make and add, then return
		ret = new Chunk(x, y, noise);
		chunkmap.put(x, y, ret);
		return ret;
	}

	public int getFloorHeight(int x) {
		int cx = pixelToChunk(x);
		return getChunk(cx, 0).getFloorHeight(x - cx * Chunk.chunkSize);
	}

	public int getFloorHeight(double x) {
		return getFloorHeight((int) Math.round(x));
	}
}
