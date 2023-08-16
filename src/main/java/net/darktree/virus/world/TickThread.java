package net.darktree.virus.world;

import net.darktree.virus.logger.Logger;

public class TickThread implements Runnable {

	private static final double interval = 1000.0 / 60.0;
	private volatile boolean flag;
	private Thread thread;
	private final World world;
	private double lastTime = 0;
	private boolean pause;

	// TPS calculation
	private int tps = 0;
	private int counter = 0;
	private long lastTpsUpdate = 0;

	public TickThread(World world) {
		this.world = world;
	}

	public TickThread start() {
		flag = true;
		thread = new Thread(this, "TickThread");
		thread.start();
		return this;
	}

	public void stop() {
		flag = false;
		try {
			thread.join();
		} catch (Exception ignore) {
		}
	}

	@Override
	public void run() {
		try {
			while (flag) {
				counter++;

				try {
					Thread.sleep((int) Math.max(interval - lastTime, 0));
				} catch (InterruptedException ignore) {
				}

				long start = System.nanoTime();

				if (!pause) {
					world.updateParticleCount();
					world.tick();
				}

				long end = System.nanoTime();
				lastTime = (double) (end - start) / 1000000.0;

				if (lastTpsUpdate + 1000 < end / 1000000) {
					lastTpsUpdate = end / 1000000;
					tps = counter;
					counter = 0;
				}
			}
		} catch (Exception exception) {
			Logger.error("Unexpected exception in tick thread!");
			exception.printStackTrace();
		}

		Logger.info("Stopped tick thread.");
	}

	public int getTPS() {
		return tps;
	}

	public void togglePause() {
		pause = !pause;
	}

	public boolean isPaused() {
		return pause;
	}

}
