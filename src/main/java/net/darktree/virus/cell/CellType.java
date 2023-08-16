package net.darktree.virus.cell;

public enum CellType {
	EMPTY(false),
	LOCKED(true),
	NORMAL(true),
	SHELL(true),
	CLEANER(false);

	private final boolean solid;

	CellType(boolean solid) {
		this.solid = solid;
	}

	public boolean isSolid() {
		return solid;
	}
}
