package terrain;

import java.awt.Graphics2D;

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
		// for testing, use no seed
		noise = new OpenSimplexNoise();
	}

	public void draw(Camera cam, Graphics2D g) {
		// Get bounds
		int minx = (int) Math.floor(((double) cam.getTLX()) / Chunk.chunkSize);
		int miny = (int) Math.floor(((double) cam.getTLY()) / Chunk.chunkSize);
		int maxx = (int) Math.ceil(((double) cam.getBRX()) / Chunk.chunkSize);
		int maxy = (int) Math.ceil(((double) cam.getBRY()) / Chunk.chunkSize);
		// Loop through and draw chunks
		for (int i = minx; i <= maxx; i++) {
			for (int j = miny; j <= maxy; j++) {
				// Draw chunk
				getChunk(i, j).draw(g);
			}
		}
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
}
