package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {



	@Override
	public void keyPressed(KeyEvent e) {
		// Check board first
		if(Game.board != null && Game.board.pressKey(e.getKeyCode())) {
			return;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Check board first
		if(Game.board != null && Game.board.releaseKey(e.getKeyCode())) {
			return;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Unused
	}

}
