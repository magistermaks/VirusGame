package net.darktree.virus.world;

import net.darktree.virus.logger.Logger;

public class TickThread implements Runnable {

    private static final double interval = 1000.0 / 60.0;

    private final Thread thread;
    private final World world;
    private boolean flag;
    private double lastTime = 0;

    // TPS calculation
    private int tps = 0;
    private int counter = 0;
    private long lastTpsUpdate = 0;

    public TickThread(World world) {
        this.thread = new Thread(this, "TickThread");
        this.world = world;
    }

    public TickThread start() {
        this.flag = true;
        thread.start();
        return this;
    }

    public void stop() {
        this.flag = false;
    }

    @Override
    public void run() {
        while( flag ) {
            counter ++;

            try{
                Thread.sleep( (int) Math.max( interval - lastTime, 0 ) );
            }catch(InterruptedException ignore) {}

            long start = System.nanoTime();
            world.updateParticleCount();
            world.tick();
            long end = System.nanoTime();
            lastTime = (double) (end - start) / 1000000.0;

            if( lastTpsUpdate + 1000 < end / 1000000 ) {
                lastTpsUpdate = end / 1000000;
                tps = counter;
                counter = 0;
            }
        }

        Logger.info("Stopped tick thread.");
    }

    public int getTPS() {
        return tps;
    }

}
