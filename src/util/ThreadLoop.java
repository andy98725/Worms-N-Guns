package util;

import main.Game;

public abstract class ThreadLoop implements Runnable {

	// Fraemrate (default 60)
	private double frameRate = 60;
	// Main thread
	private Thread loop;

	// Make and run thread
	public void startThread() {
		loop = new Thread(this);
		loop.start();
	}

	// Make and run named thread
	public void startThread(String name) {
		loop = new Thread(this, name);
		loop.start();
	}
	// Stop thread
	public void interruptThread() {
		loop.interrupt();
	}
	public void setFrameRate(double frame) {
		frameRate = frame;
	}

	// Timing
	public double delta, runTime;
	// Frame count
	public int frameCount;

	// Main loop
	@Override
	public void run() {
		// Declare timing variables
		long prevFrame = System.nanoTime(), sleep = 0;
		runTime = 0;
		while (Game.running && !Thread.currentThread().isInterrupted()) {
			// Nanoseconds to seconds
			delta = (System.nanoTime() - prevFrame) / 1000000000.0;
			runTime += delta;
			// Update frame timing
			prevFrame = System.nanoTime();
			frameCount++;
			// Do loop
			doLoop();
			// Calculate pause time (in MS)
			sleep = (long) (1000.0 / frameRate) - (System.nanoTime() - prevFrame) / 1000000;
			if (sleep < 0)
				sleep = 0;
			// Delay frame
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				System.out.println("Interrupted: " + e.getMessage());
				Debug.log("Interrupted: " + e.getMessage());
				return;
			}

		}
	}

	// Loop action
	protected abstract void doLoop();

}
