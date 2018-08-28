package main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import util.Debug;

public class Texturepack {
	protected String source;
	Map<String, BufferedImage> imgCache;
	protected double graphicsQualityScale = 1;
	protected int constructionBorder = 4;

	public Texturepack(String src) {
		// Get source dir and set up image cache
		source = src;
		imgCache = new HashMap<String, BufferedImage>();
		// Parse settings file
		parseSettings();
		// Load images
		loadImages();
	}

	// Parse settings
	protected void parseSettings() {
		BufferedReader br = null;
		String st;
		// Try to load it
		try {
			br = new BufferedReader(new FileReader(new File(source + "settings.txt")));
		} catch (FileNotFoundException e) {
			Debug.log("Settings file does not exist. Loading defaults");
			return;
		}
		// Loop through
		try {
			while ((st = br.readLine()) != null) {
				// Parse by word
				String command[] = st.split(" ", 2);
				// Check length
				if (command.length < 2) {
					continue;
				}
				// Depending on contents, switch
				switch (command[0]) {
				default: // Unrecognized
					// Comment check
					if (command[0].charAt(0) == '/' && command[0].charAt(1) == '/') {
						continue;
					}
					// Log unrecognized
					Debug.log("Texture command not recognized:");
					Debug.log(command[0]);
					Debug.log("Texture pack will not initialize this.");
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Load in all sprites to memory
	public void loadImages() {
		// TODO: Hard load any images
	}

	public BufferedImage getImage(String loc) {
		// Check if in cache
		if (imgCache.containsKey(loc)) {
			return imgCache.get(loc);
		}
		// Load from file
		BufferedImage ret;
		try {
			ret = ImageIO.read(new File(source + loc));
		} catch (IOException e) {
			throw new RuntimeException("File not found at " + source + loc);
		}
		// Save to cache
		imgCache.put(loc, ret);
		// Return
		return ret;
	}

	// Terrain colors:
	public Color skyColor = new Color(120, 170, 255);
	public Color cloudColor = new Color(200, 200, 220);
	public Color soilColor = new Color(200, 150, 100);
	public Color dirtColor = new Color(120, 80, 40);
	public Color darkDirtColor = new Color(100, 50, 0);
	public Color ironColor = new Color(160, 160, 170);
	public Color goldColor = new Color(200, 180, 0);

	public double getQuality() {
		return graphicsQualityScale;
	}
}
