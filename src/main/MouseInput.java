package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInput implements MouseListener, MouseMotionListener {

	// For some reason, it doesn't initialize at 0,0. Offsets are needed.
	private static final int xoff = 0, yoff = -32;

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// Try use on game board
		if (Game.board != null && Game.board.mouseMove(arg0.getX() + xoff, arg0.getY() + yoff)) {
			return;
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// Try use on game board
		if (Game.board != null && Game.board.mousePress()) {
			return;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Try use on game board
		if (Game.board != null && Game.board.mouseRelease()) {
			return;
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// Unused
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// Unused
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Unused
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// Unused
	}

}
