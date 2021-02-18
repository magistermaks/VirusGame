package net.darktree.virus.cell;

public enum CellType {
    Empty,
    Locked,
    Normal,
    Shell;

    public boolean isAlive() {
        return this == Normal || this == Shell;
    }

    public boolean isHurtable() {
        return this == Normal;
    }

}
