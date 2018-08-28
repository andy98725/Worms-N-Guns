package main;

import main.Game;
import main.ScreenRender;

public abstract class GlobalSettings {

	static boolean isFullscreen;
	static double graphicsQuality;
	static boolean keyboardControls;
	// Initialize defaults
	public static void initDefaults() {
		// Set rendering detail
		setGraphicsQuality(1);
		// Is fullscreen?
		setIsFullscreen(false);
		// Set keyboard controls
		setKeyboardControls(false);
		
	}
	public static double getGraphicsQuality() {
		return graphicsQuality;
	}
	public static void setGraphicsQuality(double set) {
		graphicsQuality = set;
		Game.updateGraphicsQuality();
	}
	public static boolean getIsFullscreen() {
		return isFullscreen;
	}
	public static void setIsFullscreen(boolean set) {
		if(set != isFullscreen) {
			isFullscreen = set;
			ScreenRender.refreshScreenDisplay();
		}
	}
	public static boolean getKeyboardControls() {
		return keyboardControls;
	}
	public static void setKeyboardControls(boolean set) {
		keyboardControls = set;
	}
}
