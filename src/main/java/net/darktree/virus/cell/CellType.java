package net.darktree.virus.cell;

public enum CellType {
    Empty(false),
    Locked(true),
    Normal(true),
    Shell(true),
    Kill(false);

    private final boolean solid;

    CellType(boolean solid) {
        this.solid = solid;
    }

    public boolean isSolid() {
        return solid;
    }
}
