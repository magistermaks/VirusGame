package net.darktree.virus.util;

public enum Direction {

    UP(0, 0, -1),
    DOWN(1, 0, 1),
    LEFT(2, -1, 0),
    RIGHT(3, 1, 0);

    public final int x;
    public final int y;
    public final int i;

    Direction(int i, int x, int y) {
        this.i = i;
        this.x = x;
        this.y = y;
    }

    public static Direction getFrom( int id ) {
        switch(id) {
            case 0: return UP;
            case 1: return DOWN;
            case 2: return LEFT;
            case 3: return RIGHT;
            default: return null;
        }
    }

    public static Direction getRandom() {
        return getFrom( Utils.random(4) );
    }

    public Direction cycle() {
        int j = i + 1;
        return getFrom( j > 3 ? 0 : j );
    }

}
