package net.darktree.virus.cell;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.ui.Screen;
import net.darktree.virus.util.DrawContext;
import net.darktree.virus.world.World;

public abstract class Cell implements DrawContext {

	public final int x;
	public final int y;

	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public abstract CellType getType();

	public void draw(Screen screen) {

		float posx = screen.trueXtoAppX(x);
		float posy = screen.trueYtoAppY(y);

		// only draw cells that are visible on screen
		if (posx < -screen.camS || posy < -screen.camS || posx > screen.maxRight || posy > Main.applet.height) {
			return;
		}

		push();
		translate(posx, posy);
		scale(screen.camS / Const.BIG_FACTOR);
		noStroke();
		drawCell(screen);

		pop();
		unscaledDraw(screen);

	}

	protected void drawCell(Screen screen) {

	}

	protected void unscaledDraw(Screen screen) {

	}

	public void tick() {

	}

	public void die(boolean silent) {
		if (this == Main.applet.editor.selected) {
			Main.applet.editor.close();
		}

		World world = Main.applet.world;
		if (!silent) world.getStats().DEATHS.increment();
		world.remove(this);
	}

	public String getCellName() {
		return "Empty";
	}

	// used only in one place, but I will keep it for now
	public static class Factory {

		private final int x;
		private final int y;
		private final CellType type;

		private String dna = Const.DEFAULT_CELL_GENOME;

		private Factory(int x, int y, CellType type) {
			this.x = x;
			this.y = y;
			this.type = type;
		}

		public static Factory of(int x, int y, CellType type) {
			return new Factory(x, y, type);
		}

		public Factory dna(String dna) {
			this.dna = dna;
			return this;
		}

		public Cell build() {
			switch (type) {
				case EMPTY:
					return null;
				case SHELL:
					return new ShellCell(x, y);
				case LOCKED:
					return new WallCell(x, y);
				case NORMAL:
					return new NormalCell(x, y, dna);
				case CLEANER:
					return new KillCell(x, y);
			}

			return null;
		}

	}

}
