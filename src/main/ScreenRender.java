package main;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

public abstract class ScreenRender {
	private static int frameWid, frameHei;

	public static void refreshScreenDisplay() {
		// Check if window refresh is needed
		if (GlobalSettings.getIsFullscreen() != WormsNGuns.getIsFullscreen()) {
			WormsNGuns.refreshWindow(GlobalSettings.getIsFullscreen());
			return;
		}
		// Calc dimensions
		frameWid = WormsNGuns.frame.getWidth();
		frameHei = WormsNGuns.frame.getHeight();

		// Do sizing updates
		if (WormsNGuns.game != null) {
			WormsNGuns.game.setDimensions(frameWid, frameHei);
		}
		if (Game.board != null) {
			Game.board.setDimensions(frameWid, frameHei);
		}
	}

	public static void addResizeListener(JFrame frame) {
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				refreshScreenDisplay();
			}
		});
	}
}
