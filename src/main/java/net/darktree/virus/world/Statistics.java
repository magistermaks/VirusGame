package net.darktree.virus.world;

import net.darktree.virus.cell.CellType;

public class Statistics {

    public final Stat INITIAL = new Stat("Initial");
    public final Stat DEATHS = new Stat("Deaths");
    public final Stat BIRTHS = new Stat("Births");
    public final Stat FOODS = new Stat("Foods");
    public final Stat WASTES = new Stat("Wastes");
    public final Stat VIRUSES = new Stat("Viruses");
    public final Stat INFECTIONS = new Stat("Infections");
    public final Stat ALIVE = new Stat("Alive");
    public final Stat SHELL = new Stat("Shell");

    Statistics( int initialCount ) {
        INITIAL.set(initialCount);
    }

    public void update( World world ) {
        int s = world.getSize();
        int alive = 0;
        int shell = 0;

        for( int x = 0; x < s; x ++ ) {
            for( int y = 0; y < s; y ++ ) {
                CellType type = world.getCellTypeAt(x, y);

                if( type == CellType.Normal ) alive ++;
                if( type == CellType.Shell ) shell ++;
            }
        }

        ALIVE.set(alive);
        SHELL.set(shell);
    }

    public static class Stat {

        private int record = 0;
        private final String name;

        private Stat(String name) {
            this.name = name;
        }

        public void increment() {
            record ++;
        }

        private void set( int i ) {
            record = i;
        }

        public String get() {
            return name + ": " + record;
        }

        public int count() {
            return record;
        }

    }

}
